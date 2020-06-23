#!/bin/sh

ORDER_PROCESSOR_PID_FILE="./order-processor.pid"
JAVA_HOME=../../jdk-11.0.6/
JAR_NAME=./order-processor-1.0-SNAPSHOT.jar
JAVA_CMD="${JAVA_HOME}/bin/java -jar ${JAR_NAME}"
LOG_FILE=order-processor.log

RETVAL=0

case "$1" in
    start)
    	if [ -f "$ORDER_PROCESSOR_PID_FILE" ]; then
    		echo Order processor is already running, stop it first.
    		exit 1
		fi
        echo "Starting order-processor"
        ${JAVA_CMD} > ${LOG_FILE} 2>&1 &
        RETVAL=$?
        PID=$!
        if [ $RETVAL = 0 ]; then
            echo $PID > "$ORDER_PROCESSOR_PID_FILE"
            echo Order processor started, see log in ${LOG_FILE}
        else
            failure
        fi
        echo
        ;;
    stop)
        echo -n "Shutting down order-processor "
        cat ${ORDER_PROCESSOR_PID_FILE}
        kill -9 `cat ${ORDER_PROCESSOR_PID_FILE}`
        rm ${ORDER_PROCESSOR_PID_FILE}
        RETVAL=$?
        echo
        ;;
    restart)
        $0 stop
                sleep 5
        $0 start
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac
exit $RETVAL
