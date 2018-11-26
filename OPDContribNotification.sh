export OPDL=./dist/lib/

# Send notification to contributors (0) or OpenProdoc users (1)
CASE=0

# Date of the last check in milliseconds (e.g., 3600000 ms to check new contributions in the last 1 hour).
# 1 hour
MS=3600000
# 1 day
# MS=86400000
# 1 week
# MS=604800000

java -Dfile.encoding=UTF-8 -classpath .:$OPDLProdoc.jar:$OPDLProdocSwing.jar:$OPDLjavax.mail.jar:$OPDLtika-app-1.18.jar:./dist/OPDContribNotification.jar: opdcontribnotification.OPDContribNotification $CASE $MS