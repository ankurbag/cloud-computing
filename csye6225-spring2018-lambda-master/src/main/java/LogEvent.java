import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class LogEvent implements RequestHandler<SNSEvent, Object> {

  //***********************************
  private DynamoDB dynamoDB;
  private String DYANAMO_TABLE_NAME = "csye6225";
  private Regions REGION = Regions.US_EAST_1; //check region again
  public LambdaLogger logger;

  //***********************************

  public Object handleRequest(SNSEvent request, Context context) {

    //***********************************
    this.initDynamoDbClient();
    logger = context.getLogger();
    //***********************************

    String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());

    logger.log("Invocation started: " + timeStamp);

    logger.log("Records size: " + (request.getRecords().size()));

    logger.log("Payload data: " + request.getRecords().get(0).getSNS().getMessage());

    logger.log("Invocation completed: " + timeStamp);

    //******************************
    UUID uuid = UUID.randomUUID();

    //String userid = uuid.toString();

    logger.log("UUID: " + uuid);

    String username = request.getRecords().get(0).getSNS().getMessage();

    logger.log("username: " + username);

    Table table = this.dynamoDB.getTable(DYANAMO_TABLE_NAME);
    logger.log("Table: " + table);

      // number of seconds elapsed since 12:00:00 AM January 1st, 1970 UTC.
      Long currenttime = new Long(new Date().getTime());
      currenttime /= 1000;
      logger.log("Current Time: " + currenttime);
      //Adding 20 mins to current time
      Long expirationtime = currenttime + 1200;

      QuerySpec spec = new QuerySpec()
              .withKeyConditionExpression("userid = :v_uid and expirationtime > :v_currenttime")
              .withValueMap(new ValueMap()
                      .withString(":v_uid", username)
                      .withNumber(":v_currenttime", currenttime));
      logger.log("Spec: " + spec);
      ItemCollection<QueryOutcome> items = table.query(spec);
      logger.log("Items: " + items);
      Iterator<Item> iterator = items.iterator();

    // if token does not already exist
    if (!iterator.hasNext())
    {
      try {
        String FROM = "donotreply@csye6225-spring2018-zaveriv.me";
        String TO = request.getRecords().get(0).getSNS().getMessage();
        String token = request.getRecords().get(0).getSNS().getMessageId();

        this.dynamoDB.getTable(DYANAMO_TABLE_NAME).putItem(
                new PutItemSpec().withItem(new Item()
                        .withPrimaryKey("userid", username)
                        .withString("token", uuid.toString())
                        .withLong("expirationtime", expirationtime)));

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_EAST_1)
                        .build();
        logger.log("SES client initialized: " + client);
        SendEmailRequest req = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(TO))
                .withMessage(new Message().withBody(
                        new Body().withHtml(new Content()
                                .withCharset("UTF-8").withData(
                                        "Please click on the below link to reset the password<br/>" +
                                                "<p><a href='#'>https://csye6225-spring2018-zaveriv.me/reset?email=" + TO + "&token=" + uuid + "</a></p>"))
                )
                        .withSubject(new Content().withCharset("UTF-8").withData("Password Reset Link")))
                .withSource(FROM);
        SendEmailResult response = client.sendEmail(req);
        context.getLogger().log("Email sent!");
      } catch (Exception ex) {
        context.getLogger().log("The email was not sent. Error message: " + ex.getMessage());
      }

    }


    //********************************
    return null;

  }

  //***********************************
  private void initDynamoDbClient() {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1).build();
    this.dynamoDB = new DynamoDB(client);
  }
  //***********************************


}

