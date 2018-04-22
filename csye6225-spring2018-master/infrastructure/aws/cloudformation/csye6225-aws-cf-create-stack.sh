echo "CloudFormation script started to set up stack"

if [ -z "$1" ]
  then
    echo "Please give STACK_NAME as input parameter to script!"
    exit 1
fi

stack_name="$1"
echo "Your stack name: " $stack_name
SD=$(aws cloudformation describe-stacks)
echo "Initial Stack Details Value: " $SD

SD=$(aws cloudformation validate-template --template-body file://./csye6225-cf-networking.json)
if [ $? -eq '0' ]
  then
    echo "Template validated"
else
    echo "Invalid template or not template found"
    exit 1
fi

SD=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://./csye6225-cf-networking.json --parameters ParameterKey=VPCNAME,ParameterValue=$stack_name-csye6225-vpc ParameterKey=IGWNAME,ParameterValue=$1-csye6225-InternetGateway ParameterKey=ROUTETABLENAME,ParameterValue=$1-csye6225-public-route-table ParameterKey=ROUTETABLENAMEPRIVATE,ParameterValue=$1-csye6225-private-route-table ParameterKey=DBSecurityGrp,ParameterValue=$1-csye6225-rds ParameterKey=WebSecurityGrp,ParameterValue=$1-csye6225-webapp)

echo "Setting up stack: " $stack_name-csye6225-vpc
aws cloudformation wait stack-create-complete --stack-name $stack_name

STACKDETAILS=$(aws cloudformation describe-stacks --stack-name $stack_name --query Stacks[0].StackId --output text)

echo "Stack created successfully"
echo "Stack Id: " $STACKDETAILS
exit 0

