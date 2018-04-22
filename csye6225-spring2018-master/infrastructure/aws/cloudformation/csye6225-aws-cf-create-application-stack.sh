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

SubnetID1=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query "Subnets[0].SubnetId" --output text)
echo "SUBNET ID is $SubnetID1"

SubnetID2=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query "Subnets[1].SubnetId" --output text)
echo "SUBNET ID is $SubnetID2"

SubnetID3=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query "Subnets[2].SubnetId" --output text)
echo "SUBNET ID is $SubnetID3"

SubnetID4=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query "Subnets[3].SubnetId" --output text)
echo "SUBNET ID is $SubnetID4"

CIDR1=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query "Subnets[0].CidrBlock" --output text)
echo "Cidr Block 1 is $CIDR1"

CIDR2=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query "Subnets[1].CidrBlock" --output text)
echo "Cidr Block 2 is $CIDR2"

CIDR3=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query "Subnets[2].CidrBlock" --output text)
echo "Cidr Block 2 is $CIDR3"

CIDR4=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query "Subnets[3].CidrBlock" --output text)
echo "Cidr Block 2 is $CIDR4"

CodeDeployServiceRoleArn=$(aws iam get-role --role-name CodeDeployServiceRole --query "Role.Arn" --output text)
echo "CodeDeployServicerole Arn is $CodeDeployServiceRoleArn"

echo "Public subnet id is: $PublicSubnet"
echo "Private Subnet id is: $PrivateSubnet"

GroupName=$(aws ec2 describe-security-groups --filter Name=vpc-id,Values=$VPCID --query 'SecurityGroups[*].{Name:GroupName}' --output text)
echo $GroupName

GroupIdRDS=$(aws ec2 describe-security-groups --filters "Name=tag:Name,Values=$networking_stack_name-csye6225-rds" --query "SecurityGroups[*].GroupId" --output text)
echo "Group ID is: $GroupIdRDS"

GroupIdEC2=$(aws ec2 describe-security-groups --filters "Name=tag:Name,Values=$networking_stack_name-csye6225-webapp" --query "SecurityGroups[*].GroupId" --output text)
echo "Group id for EC2 is: $GroupIdEC2"

CertificateArn=$(aws acm list-certificates --query 'CertificateSummaryList[0].CertificateArn' --output text)
echo "Certificate Arn: $CertificateArn"

#Endpoint=$(aws rds describe-db-instances --query "DBInstances[*].Endpoint.Address" --output text)
#echo "Endpoint URL is $Endpoint"

SD=$(aws cloudformation create-stack --stack-name $stack_name --capabilities CAPABILITY_NAMED_IAM --template-body file://./csye6225-cf-application.json --parameters ParameterKey=IMAGEID,ParameterValue=ami-66506c1c ParameterKey=INSTANCETYPE,ParameterValue=t2.micro ParameterKey=VOLUMETYPE,ParameterValue=gp2 ParameterKey=VOLUMESIZE,ParameterValue=16 ParameterKey=PRIMARYKEY,ParameterValue=userid ParameterKey=TABLENAME,ParameterValue=csye6225 ParameterKey=S3BUCKETNAME,ParameterValue=web-app.$DNS ParameterKey=ALLOCATEDSTORAGE,ParameterValue=$ALLOCATEDSTORAGE ParameterKey=DBNAME,ParameterValue=$DBNAME ParameterKey=ENGINE,ParameterValue=$ENGINE ParameterKey=ENGINEVERSION,ParameterValue=$ENGINEVERSION ParameterKey=DBInstanceClass,ParameterValue=$DBInstanceClass ParameterKey=DBInstanceIdentifier,ParameterValue=$DBInstanceIdentifier ParameterKey=USERNAME,ParameterValue=$USERNAME ParameterKey=PASSWORD,ParameterValue=$PASSWORD ParameterKey=SubnetId,ParameterValue=$PublicSubnet ParameterKey=SubnetId2,ParameterValue=$PrivateSubnet ParameterKey=SubnetId3,ParameterValue=$PrivateSubnet2 ParameterKey=GroupIdRDS,ParameterValue=$GroupIdRDS ParameterKey=EC2SecurityGroup,ParameterValue=$GroupIdEC2 ParameterKey=Keyname,ParameterValue=$KEYNAME ParameterKey=EC2NAME,ParameterValue=EC2NAME ParameterKey=VpcId,ParameterValue=$VPCID ParameterKey=CertificateArn,ParameterValue=$CertificateArn ParameterValue=$DID ParameterKey=TargetGroupName,ParameterValue=$TARGETGROUPNAME ParameterKey=DNS,ParameterValue=$DNS ParameterKey=CodeDeployServiceRoleArn,ParameterValue=$CodeDeployServiceRoleArn)

echo "Setting up stack: " $stack_name-csye6225-vpc
aws cloudformation wait stack-create-complete --stack-name $stack_name

STACKDETAILS=$(aws cloudformation describe-stacks --stack-name $stack_name --query Stacks[0].StackId --output text)

echo "Stack created successfully"
echo "Stack Id: " $STACKDETAILS
exit 0

