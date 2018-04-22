#!/bin/bash

if [ -z "$1" ]
then
	echo "Please provide command line argument for STACK_NAME"
	exit 1
fi

echo "Setting up VPC"
VPCNAME="$1-csye6225-vpc"

RC=$(aws ec2 create-vpc --cidr-block 10.0.0.0/16)

if [ $? -eq 0 ]
then
	echo "VPC created succssfully!"
else
	echo "VPC creation failed"
	exit 1
fi


VPCID=$(aws ec2 describe-vpcs  --filters Name=cidr,Values=10.0.0.0/16 --query Vpcs[0].VpcId --output text)

if [ $? -eq 0 ]
then
	echo "VPC described successfully"
else
	echo "VPC describe operation failed"
	exit 1
fi

echo "VPC ID: "$VPCID
echo "VPC NAME: "$VPCNAME

RC=$(aws ec2 create-tags --resources $VPCID --tags Key=Name,Value=$VPCNAME)

if [ $? -eq 0 ]
then
	echo "Successfully created tag for VPC ID: "$VPCID
else
	echo "Failed in creating tag"
	exit 1
fi


IGWNAME="$1-csye6225-InternetGateway"
RC=$(aws ec2 create-internet-gateway)

if [ $? -eq 0 ]
then
	echo "Internet Gateway created successfully"
else
	echo "InternetGateway creation failed"
	exit 1
fi

IGWID=$(aws ec2 describe-internet-gateways --query 'InternetGateways[?Attachments[0].State != `available`]'.InternetGatewayId --output text)

if [ $? -eq 0 ]
then
	echo "Internet gateway described successfully"
else
	echo "Internet gateway describe operation failed"
	exit 1
fi

RC=$(aws ec2 create-tags --resources $IGWID --tags Key=Name,Value=$IGWNAME)

if [ $? -eq 0 ]
then
	echo "Successfully created tag for InternetGateway"
else
	echo "Failed in creating tag for InternetGateway"
	exit 1
fi

RC=$(aws ec2 attach-internet-gateway --internet-gateway-id $IGWID --vpc-id $VPCID)

if [ $? -eq 0 ]
then
	echo "InternetGateway attached successfully"
else
	echo "Failed in attaching InternetGateway"
	exit 1
fi

ROUTETABLENAME="$1-csye6225-public-route-table"
RC=$(aws ec2 create-route-table --vpc-id $VPCID)

if [ $? -eq 0 ]
then
	echo "Successfully created RouteTable"
else
	echo "Failed in creation of RouteTable"
	exit 1
fi

ROUTETABLEID=$(aws ec2 describe-route-tables --filters Name=vpc-id,Values=$VPCID --query 'RouteTables[?Associations[0].Main != `true`]'.RouteTableId --output text)

if [ $? -eq 0 ]
then
	echo "RouteTable describe successfully"
else
	echo "Failed in creation of describe operation"
	exit 1
fi

RC=$(aws ec2 create-tags --resources $ROUTETABLEID --tags Key=Name,Value=$ROUTETABLENAME)

if [ $? -eq 0 ]
then
	echo "Successfully created tag for route table"
else
	echo "Failed in creating tag for route table"
	exit 1
fi

RC=$(aws ec2 create-route --route-table-id $ROUTETABLEID --destination-cidr-block 0.0.0.0/0 --gateway-id $IGWID)

if [ $? -eq 0 ]
then
	echo "Successfully created route for route table: "$ROUTETABLEID
else
	echo "Failed in creating route for route table: "$ROUTETABLEID
	exit 1
fi

echo "VPC setup completed successfully"
