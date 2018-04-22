# Steps to run the scripts-

# csye6225-aws-cf-create-stack.sh

 Shell script to create networking stack. It creates VPC, gateway, route tabls( private and public), routes, subnets, security   group.

1. To run create cloud formation script-
  a. In terminal, give command as follow-
     sh csye6225-aws-cf-create-stack.sh <STACK_NAME>

2. To terminate cloud formation stack-
   a. In temrinal, give command as follows-
      sh csye6225-aws-cf-terminate-stack.sh <STACK_NAME>

# csye6225-aws-cf-create-ci-cd-stack.sh

This script create IAM resources list as follows: 
1. IAM roles for EC2 & Codedeploy
2. IAM Policy for EC2 & Codedeploy
3. Attaches the policy to IAM user (Travis)
4. S3 bucket for catching .zip file from travis
5. Codedeploy application so that it could put files from s3 to ec2 instance
6. SNS Topic and Lambda execution role created to support serverless computing
7. SES and DynamoDb full access policy provided to lambda execution role to write in Dynamodb and to send email through SES
8. Cloudwatch policy attached to EC2 through code deploy EC2 role

To run the script- 
 a. On terminal enter following comand:
   sh csye6225-aws-cf-create-ci-cd-stack.sh <STACK_NAME>

To terminate the ci-cd stack-
 a. On terminal enter the following command-
   sh csye6225-aws-cf-terminate-ci-cd-stack.sh <STACK_NAME>

# csye6225-aws-cf-create-application-stack.sh

Shell script to create application stack. Following are the instances created using this script-
1. Autoscaling Group
2. DynamoDB
3. S3 Bucket
4. RDS
5. Code deploy Application 
6. Load Balancer & Target Group
7. Resource Recordset

Updated user data to install and configure cloud watch agent on EC2

To run create cloud formation script-
  a. In terminal, give command as follow-
     sh csye6225-aws-cf-create-application-stack.sh <STACK_NAME> <NETWORKING_STACK_NAME>
      
 To terminate cloud formation stack-
   a. In temrinal, give command as follows-
      sh csye6225-aws-cf-create-application-stack.sh <STACK_NAME>
      


Note: The order of execution of above scripts should be maintained as follows:
1.  csye6225-aws-cf-create-stack.sh <STACK_NAME>
2.  csye6225-aws-cf-create-ci-cd-stack.sh <STACK_NAME>
3.  csye6225-aws-cf-create-appliaction-stack.sh <APP_STACK_NAME> <NW_STACK_NAME>


