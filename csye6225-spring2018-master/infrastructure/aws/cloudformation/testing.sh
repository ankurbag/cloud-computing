echo "CloudFormation script started to set up stack"

if [ -z "$1" ]
  then
    echo "Please give STACK_NAME as input parameter to script!"
    exit 1
fi


if [ -z "$2" ]
  then
    echo "Please give networking STACK_NAME as second input parameter to script!"
    exit 1
fi

stack_name="$1"
networking_stack_name="$2"

echo "Your stack name: " $stack_name
echo "Your networking stack name: " $networking_stack_name

SD=$(aws cloudformation describe-stacks)
echo "Initial Stack Details Value: " $SD

SD=$(aws cloudformation validate-template --template-body file://./csye6225-cf-application.json)
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

DOMAINID=$(aws route53 list-hosted-zones --query HostedZones[0].Id --output text)
DID=${DOMAINID#*e/}
echo $DID

ALLOCATEDSTORAGE=20
DBNAME=csye6225
ENGINE=MySQL
ENGINEVERSION=5.6.37
DBInstanceClass=db.t2.medium
DBInstanceIdentifier=csye6225-spring2018
USERNAME=csye6225master
PASSWORD=csye6225password
KEYNAME=credentials
TARGETGROUPNAME=csye6225-target-group-name

SID=$(aws cloudformation describe-stacks --stack-name $networking_stack_name --query Stacks[0].StackId --output text)
echo "stack id is $SID"

VPCID=$(aws ec2 describe-vpcs --filter "Name=tag:aws:cloudformation:stack-id,Values=$SID" --query Vpcs[0].VpcId --output text)
echo "VPCID is $VPCID"

Subnetss=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --output text)
i=0
for subnet in $Subnetss
  do
    echo "${subnet}" | jq -c '.[]'
  done