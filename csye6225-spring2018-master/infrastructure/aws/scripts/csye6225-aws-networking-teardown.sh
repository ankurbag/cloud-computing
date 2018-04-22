
if [ -z "$1" ]
then
	echo "Please provide command line argument for STACK_NAME"
	exit 1
fi


ROUTETABLENAME="$1-csye6225-public-route-table"

ROUTETABLEID=$(aws ec2 describe-route-tables --filters Name=tag:Name,Values=$ROUTETABLENAME --query RouteTables[0].RouteTableId --output text)

if [ $? -eq 0 ]
then
	echo "Success"
else
	echo "Fail describe route table id"
	exit 1
fi

RC=$(aws ec2 delete-route --route-table-id $ROUTETABLEID --destination-cidr-block 0.0.0.0/0)

if [ $? -eq 0 ]
then
	echo "Successfully deleted route"
else
	echo "Failed in deleting route"
	exit 1
fi

RC=$(aws ec2 delete-route-table --route-table-id $ROUTETABLEID)

if [ $? -eq 0 ]
then
	echo "Successfully deleted route table"
else
	echo "Failed in deleting route table"
	exit 1
fi


IGWNAME="$1-csye6225-InternetGateway"

VPCNAME="$1-csye6225-vpc"

VPCID=$(aws ec2 describe-vpcs --filters Name=tag:Name,Values=$VPCNAME --query Vpcs[0].VpcId --output text)

if [ $? -eq 0 ]
then
	echo "Success"
else
	echo "Fail describe vpc id"
	exit 1
fi

IGWID=$(aws ec2 describe-internet-gateways --filters Name=attachment.vpc-id,Values=$VPCID --query InternetGateways[0].InternetGatewayId --output text)

if [ $? -eq 0 ]
then
	echo "Success in describing internet gateway"
else
	echo "Failed in describing internet gateway"
	exit 1
fi

RC=$(aws ec2 detach-internet-gateway --internet-gateway-id $IGWID --vpc-id $VPCID)

if [ $? -eq 0 ]
then
	echo "Successfully detached internet gateway from VPC"
else
	echo "Failed in detaching internet gateway from VPC"
	exit 1
fi

RC=$(aws ec2 delete-internet-gateway --internet-gateway-id $IGWID)

if [ $? -eq 0 ]
then
	echo "Successfully deleted internet gateway"
else
	echo "Failed in deleting internet gateway"
	exit 1
fi

RC=$(aws ec2 delete-vpc --vpc-id $VPCID)

if [ $? -eq 0 ]
then
	echo "Successfully deleted VPC"
else
	echo "Failed in deleting VPC"
	exit 1
fi

