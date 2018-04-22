if [ -z "$1" ]
then
	echo "Please provide STACK_NAME parameter to the script"
	exit 1
else
	echo "Deleting Stack and its resources"
fi

SD=$(aws cloudformation describe-stacks --stack-name $1 --query Stacks[0].StackId --output text) || echo "Stack $1 doesn't exist"
echo "Deleting stack: $SD"

EC2=$(aws ec2 describe-instances --filter "Name=tag:aws:cloudformation:stack-id,Values=$SD" --query 'Reservations[*].Instances[*].{id:InstanceId}' --output text) || echo "Couldn't find stack $1"


DOMAINNAME=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
DNS=${DOMAINNAME%.}

bucket=web-app.$DNS
echo $bucket

aws s3 rm s3://$bucket/ --recursive

echo "EC2 instance id is: $EC2"

echo "modifying attribute DisableApiTermination"
aws ec2 modify-instance-attribute --instance-id $EC2 --no-disable-api-termination

aws cloudformation delete-stack --stack-name $1
echo "Stack is deleting. Please wait"

SD=$(aws cloudformation wait stack-delete-complete --stack-name $1)
if [ $? -eq 0 ]
then
  echo "Stack deleted"
  exit 0
else
 	echo "Failed in deleting stack"
 	exit 1
fi
