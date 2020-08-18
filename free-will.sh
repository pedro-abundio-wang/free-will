#!/bin/sh

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
  JAVA_PATH=`which java 2>/dev/null`
  if [ "x$JAVA_PATH" != "x" ]; then
    JAVA_PATH=`dirname $JAVA_PATH 2>/dev/null`
    JAVA_HOME=`dirname $JAVA_PATH 2>/dev/null`
    JRE_HOME=`dirname $JAVA_PATH 2>/dev/null`
  fi
  if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
    echo "Neither the JAVA_HOME nor the JRE_HOME environment variable is defined"
    echo "At least one of these environment variable is needed to run this program"
    exit 1
  fi
fi

# Set standard commands for invoking Java, if not already set.
if [ -z "$_RUNJAVA" ]; then
  _RUNJAVA="$JRE_HOME"/bin/java
fi

[ -z "$FREE_WILL_PID" ] && FREE_WILL_PID="$PRGDIR"/free-will.pid

JAVA_OPTS="$JAVA_OPTS"

unset _NOHUP
_NOHUP=nohup

if [ "$1" = "start" ] ; then

  if [ ! -z "$FREE_WILL_PID" ]; then
    if [ -f "$FREE_WILL_PID" ]; then
      if [ -s "$FREE_WILL_PID" ]; then
        echo "Existing PID file found during start."
        if [ -r "$FREE_WILL_PID" ]; then
          PID=`cat "$FREE_WILL_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "free will appears to still be running with PID $PID. Start aborted."
            echo "If the following process is not a free will process, remove the PID file and try again:"
            ps -f -p $PID
            exit 1
          else
            echo "removing/clearing stale PID file."
            rm -f "$FREE_WILL_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$FREE_WILL_PID" ]; then
                cat /dev/null > "$FREE_WILL_PID"
              else
                echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
          echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$FREE_WILL_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$FREE_WILL_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi

  shift

  eval $_NOHUP "\"$_RUNJAVA\"" $JAVA_OPTS \
    -jar target/free-will.jar \
    2>&1 "&"

  if [ ! -z "$FREE_WILL_PID" ]; then
    echo $! > "$FREE_WILL_PID"
  fi

  echo "free will started."

elif [ "$1" = "stop" ]; then

  shift

  SLEEP=5
  FORCE=0

  if [ "$1" = "-force" ]; then
    shift
    FORCE=1
  fi

  if [ ! -z "$FREE_WILL_PID" ]; then
    if [ -f "$FREE_WILL_PID" ]; then
      if [ -s "$FREE_WILL_PID" ]; then
        kill -0 `cat "$FREE_WILL_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          echo "PID file found but either no matching process was found or the current user does not have permission to stop the process. Stop aborted."
          exit 1
        fi
      else
        echo "PID file is empty and has been ignored."
      fi
    else
      echo "\$FREE_WILL_PID was set but the specified file does not exist. Is free will running? Stop aborted."
      exit 1
    fi
  fi

  # stop failed. Shutdown port disabled? Try a normal kill.
  if [ $? != 0 ]; then
    if [ ! -z "$FREE_WILL_PID" ]; then
      echo "The stop command failed. Attempting to signal the process to stop through OS signal."
      kill -15 `cat "$FREE_WILL_PID"` >/dev/null 2>&1
    fi
  fi

  if [ ! -z "$FREE_WILL_PID" ]; then
    if [ -f "$FREE_WILL_PID" ]; then
      while [ $SLEEP -ge 0 ]; do
        kill -0 `cat "$FREE_WILL_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          rm -f "$FREE_WILL_PID" >/dev/null 2>&1
          if [ $? != 0 ]; then
            if [ -w "$FREE_WILL_PID" ]; then
              cat /dev/null > "$FREE_WILL_PID"
              # If free will has stopped don't try and force a stop with an empty PID file
              FORCE=0
            else
              echo "The PID file could not be removed or cleared."
            fi
          fi
          echo "free will stopped."
          break
        fi
        if [ $SLEEP -gt 0 ]; then
          sleep 1
        fi
        if [ $SLEEP -eq 0 ]; then
          echo "free will did not stop in time."
          if [ $FORCE -eq 0 ]; then
            echo "PID file was not removed."
          fi
          echo "To aid diagnostics a thread dump has been written to standard out."
          kill -3 `cat "$FREE_WILL_PID"`
        fi
        SLEEP=`expr $SLEEP - 1 `
      done
    fi
  fi

  KILL_SLEEP_INTERVAL=5
  if [ $FORCE -eq 1 ]; then
    if [ -z "$FREE_WILL_PID" ]; then
      echo "Kill failed: \$FREE_WILL_PID not set"
    else
      if [ -f "$FREE_WILL_PID" ]; then
        PID=`cat "$FREE_WILL_PID"`
        echo "Killing free will with the PID: $PID"
        kill -9 $PID
        while [ $KILL_SLEEP_INTERVAL -ge 0 ]; do
            kill -0 `cat "$FREE_WILL_PID"` >/dev/null 2>&1
            if [ $? -gt 0 ]; then
                rm -f "$FREE_WILL_PID" >/dev/null 2>&1
                if [ $? != 0 ]; then
                    if [ -w "$FREE_WILL_PID" ]; then
                        cat /dev/null > "$FREE_WILL_PID"
                    else
                        echo "The PID file could not be removed."
                    fi
                fi
                echo "The free will process has been killed."
                break
            fi
            if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
                sleep 1
            fi
            KILL_SLEEP_INTERVAL=`expr $KILL_SLEEP_INTERVAL - 1 `
        done
        if [ $KILL_SLEEP_INTERVAL -lt 0 ]; then
            echo "free will has not been killed completely yet. The process might be waiting on some system call or might be UNINTERRUPTIBLE."
        fi
      fi
    fi
  fi

else

  echo "Usage: free-will.sh ( commands ... )"
  echo "commands:"
  echo "  start             Start Catalina in a separate window"
  echo "  stop              Stop Catalina, waiting up to 5 seconds for the process to end"
  echo "  stop -force       Stop Catalina, wait up to 5 seconds and then use kill -KILL if still running"
  echo "Note: Waiting for the process to end and use of the -force option require that \$FREE_WILL_PID is defined"
  exit 1

fi
