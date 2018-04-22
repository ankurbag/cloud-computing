Sample lambda function using Java8.
We created Lambda function named csye6225-lambda.
- Subscribed Lambda function to SNS topic name EmailTopic
- Configuring SES service to send email to the user fetched from the topic.
- Writing userid, token and expiration time in java code to persist in DynamoDB.

How to use this code?
- Build the project
- Go to build/distributions folder
- Now open AWS lamnda service management console 
- Upload the zip file from the distributions folder
- Set the CPU usage to 1 GB
- Set the handler as classname::methodname, eg: LogEvent::handleRequest in this case
- Click save

Run the web application and click on forgot password, provide the valid email id and click submit.
Now monitor the rsult in the CloudWatch log stream.

