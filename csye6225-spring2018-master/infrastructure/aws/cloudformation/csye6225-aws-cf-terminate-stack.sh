if [ -z "$1" ]
then
	echo "Please provide STACK_NAME parameter to the script"
	exit 1
else
	echo "Deleting Stack and its resources"
fi

SD=$(aws cloudformation describe-stacks --stack-name $1 --query Stacks[0].StackId --output text) || echo "Stack $1 doesn't exist"
echo "Deleting stack: $SD"

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
