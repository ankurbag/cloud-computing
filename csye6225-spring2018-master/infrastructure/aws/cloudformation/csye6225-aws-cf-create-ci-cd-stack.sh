echo "CloudFormation script started to set up stack"

if [ -z "$1" ]
  then
    echo "Please give STACK_NAME as input parameter to script!"
    exit 1
fi

stack_name="$1"

SD=$(aws cloudformation validate-template --template-body file://./csye6225-cf-ci-cd.json)
if [ $? -eq '0' ]
  then
    echo "Template validated"
else
    echo "Invalid template or not template found"
    exit 1
fi

DOMAINNAME=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
DNS=${DOMAINNAME%.}
echo $DNS

Region=$(aws iam list-account-aliases)
echo $Region


export TravisUser=travis

echo "Your stack name: " $stack_name

SD=$(aws cloudformation create-stack --stack-name $1  --capabilities CAPABILITY_NAMED_IAM --template-body file://./csye6225-cf-ci-cd.json --parameters ParameterKey=S3BUCKETNAME,ParameterValue=code-deploy.$DNS ParameterKey=S3BUCKETNAME1,ParameterValue=web-app.$DNS ParameterKey=TravisUser,ParameterValue=$TravisUser)

echo "Setting up stack: " $stack_name-csye6225-policy
aws cloudformation wait stack-create-complete --stack-name $stack_name


echo "Stack created successfully"
