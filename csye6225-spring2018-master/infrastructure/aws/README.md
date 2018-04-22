# Steps to run the scripts-

1. To run create cloud formation script-
  a. In terminal, give command as follow-
     sh csye6225-aws-cf-create-stack.sh <STACK_NAME>

2. To terminate cloud formation stack-
   a. In temrinal, give command as follows-
      sh csye6225-aws-cf-terminate-stack.sh <STACK_NAME>

# csye6225-aws-cf-create-application-stack.sh

Shell script to create application stack. Following are the instances created using this script-
1. EC2 Instance
2. DynamoDB
3. S3 Bucket
4. RDS

To run create cloud formation script-
  a. In terminal, give command as follow-
     sh csye6225-aws-cf-create-application-stack.sh <STACK_NAME> <NETWORKING_STACK_NAME>
      
 To terminate cloud formation stack-
   a. In temrinal, give command as follows-
      sh csye6225-aws-cf-create-application-stack.sh <STACK_NAME>
      
 # csye6225-aws-cf-create-stack.sh
 
 Shell script to create networking stack. It creates VPC, gateway, route tabls( private and public), routes, subnets, security   group.
 
 To run create networking script-
  a. In terminal, give command as follow-
     sh csye6225-aws-cf-create-stack.sh <STACK_NAME>

 To terminate networking sscript-
  a. In terminal, give comman as follows-
     sh csye6225-aws-cf-terminate-stack.sh <STACK_NAME>
  
