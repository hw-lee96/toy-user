A=`lsof -t -i :8001`

# DB
if [ $A ]
then
    echo "pid = $A"
    kill -9 $A
    echo "$A kill success"
else 
    echo "port 8001 is not found"
fi