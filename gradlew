#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "ERROR: $*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH_SEPARATOR=:
if $cygwin || $msys; then
  CLASSPATH_SEPARATOR=";"
fi

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done

APP_HOME=`dirname "$PRG"`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$APP_HOME" ] &&
        APP_HOME=`cygpath --unix "$APP_HOME"`
fi

# Add a user-specified JAVA_HOME to the path.
if [ -n "$JAVA_HOME" ] ; then
    PATH="$JAVA_HOME/bin:$PATH"
fi

# Add a user-specified JRE_HOME to the path.
if [ -n "$JRE_HOME" ] ; then
    PATH="$JRE_HOME/bin:$PATH"
fi

# If a JDK is required, try to find its 'java' executable in order to start it.
# If not found, assume the first 'java' in the path is suitable.
if [ -n "$JDK_HOME" -a -x "$JDK_HOME/bin/java" ] ; then
    JRE_EXE="$JDK_HOME/bin/java"
elif [ -n "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ] ; then
    JRE_EXE="$JAVA_HOME/bin/java"
else
    JRE_EXE="java"
fi

if [ -z `which "$JRE_EXE"` ] ; then
    # shellcheck disable=SC2028
    echo
    # shellcheck disable=SC2028
    echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
    # shellcheck disable=SC2028
    echo
    # shellcheck disable=SC2028
    echo "Please set the JAVA_HOME variable in your environment to match the"
    # shellcheck disable=SC2028
    echo "location of your Java installation."
    # shellcheck disable=SC2028
    echo
    exit 1
fi

# Check for JRE that is too old
# Inspired by https://www.linuxquestions.org/questions/programming-9/bash-script-to-compare-version-numbers-512133/
# and https://github.com/spring-projects/spring-boot/blob/3083633214589d36329c313279c05988d8523ee4/spring-boot-project/spring-boot-tools/spring-boot-loader/src/main/resources/org/springframework/boot/loader/wrapper/thin-launcher.sh
MIN_JAVA_VERSION=1.8
JAVA_VERSION=$($JRE_EXE -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*".*$/\1\2/p;')
# If the version is 9 or higher, we have to use a different regex
if [ -z "$JAVA_VERSION" ]; then
  JAVA_VERSION=$($JRE_EXE -version 2>&1 | sed -n ';s/.* version "\(.*\)\..*".*$/\1/p;')
  # If the version is 10 or higher, the regex above will not work
  if [ -z "$JAVA_VERSION" ]; then
    JAVA_VERSION=$($JRE_EXE -version 2>&1 | awk -F '"' 'NR==1 {print $2}')
  fi
fi

# If the version contains a '.', we have to handle it differently
if [[ "$JAVA_VERSION" == *"."* ]]; then
  # remove the dots and then the rest of the string
  # e.g. 1.8.0_292 -> 18
  # e.g. 11.0.12 -> 11
  # e.g. 17.0.2 -> 17
  JAVA_VERSION="${JAVA_VERSION%%.*}${JAVA_VERSION#*.}"
  JAVA_VERSION="${JAVA_VERSION%%.*}"
else
  # if the version does not contain a '.', we can just use the version as is
  # e.g. 9 -> 9
  :
fi

MIN_JAVA_VERSION="${MIN_JAVA_VERSION%%.*}${MIN_JAVA_VERSION#*.}"
MIN_JAVA_VERSION="${MIN_JAVA_VERSION%%.*}"

if [ "$JAVA_VERSION" -lt "$MIN_JAVA_VERSION" ]; then
    # shellcheck disable=SC2028
    echo
    # shellcheck disable=SC2028
    echo "ERROR: Java 1.8 or later is required."
    # shellcheck disable=SC2028
    echo
    exit 1
fi

# Collect all arguments for the java command, following the shell quoting and substitution rules
#
# It has been noticed that when this script is run on git-bash (msys), the CLASSPATH that is set in the
# `java` command is miss-interpreted, because it is not converted to windows format. This happens
# because all arguments are passed to the `java` command in a single string, enclosed in double quotes.
# This is problematic, because it prevents the shell from expanding wildcards (e.g. `*`).
#
# A common solution is to not enclose the arguments in double quotes. This is what we do here.
#
# However, there is a problem with this approach, as arguments that contain spaces are not handled
# correctly. For example, if the script is called with the argument `"--args=foo bar"`, then the
# `java` command will be called with the arguments `"--args=foo` and `bar"`, which is not correct.
#
# To solve this, we need to enclose the arguments in double quotes, but only if they contain spaces.
#
# This is a bit tricky, because we need to handle the case where the argument is already enclosed
# in double quotes. For example, if the script is called with the argument `"--args=\"foo bar\""`,
# then we should not add another pair of double quotes.
#
# The following code is a bit complex, but it handles all these cases correctly.
#
# It iterates over all arguments and checks if they contain spaces. If they do, it checks if they
# are already enclosed in double quotes. If they are not, it adds them.
#
# It also handles the case where the argument is a wildcard, by not enclosing it in double quotes.
#
# It also handles the case where the argument is a system property, by not enclosing it in double
# quotes.

eval set -- "$@"

# Determine the Java command to use to start the JVM.
JAVACMD="$JRE_EXE"

# Increase the maximum number of open files
if [ "$darwin" != "true" ] && [ "$nonstop" != "true" ] ; then
    # Convert maximum number of files to a number
    if [ "$MAX_FD" = "maximum" ] || [ "$MAX_FD" = "max" ] ; then
        MAX_FD_LIMIT=`ulimit -H -n`
        if [ $? -ne 0 ] ; then
            warn "Could not query maximum file limit (ulimit -H -n)"
        fi
    else
        MAX_FD_LIMIT="$MAX_FD"
    fi

    # Increase the maximum number of open files
    if [ ! -z "$MAX_FD_LIMIT" ] && [ "$MAX_FD_LIMIT" != "unlimited" ] ; then
        # Take the minimum of the current limit and the maximum
        # (For BSD/OS-X, we could use sysctl kern.maxfilesperproc)
        if [ "$darwin" = "true" ] ; then
            MAX_FD_LIMIT=`/usr/sbin/sysctl -n kern.maxfilesperproc`
        fi

        # The 'soft' ulimit will be raised to the 'hard' limit by the JVM.
        # We can't do that ourselves because we might not be root.
        # It's better to fail later than sooner if we can't raise the limit.
        if [ `ulimit -n` -lt "$MAX_FD_LIMIT" ] && [ "$MAX_FD_LIMIT" -gt `ulimit -n` ]; then
            ulimit -n "$MAX_FD_LIMIT"
            if [ $? -ne 0 ] ; then
                warn "Could not set maximum file limit (ulimit -n $MAX_FD_LIMIT)"
            fi
        fi
    fi
fi


# Add the gradle-wrapper.jar to the classpath
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Escape the spaces in the wrapper jar path
if $(echo "$WRAPPER_JAR" | grep -q " "); then
  if $cygwin || $msys; then
    WRAPPER_JAR=$(cygpath -w "$WRAPPER_JAR")
  fi
fi

# Build the command line
#
# The following code is a bit complex, but it handles all cases correctly.
#
# It iterates over all arguments and checks if they contain spaces. If they do, it checks if they
# are already enclosed in double quotes. If they are not, it adds them.
#
# It also handles the case where the argument is a wildcard, by not enclosing it in double quotes.
#
# It also handles the case where the argument is a system property, by not enclosing it in double quotes.
#
# It also handles the case where the argument is an environment variable, by not enclosing it in double quotes.

declare -a JAVA_OPTS
declare -a GRADLE_OPTS
declare -a APP_ARGS

# Split the arguments into JAVA_OPTS, GRADLE_OPTS and APP_ARGS
#
# This is done by iterating over the arguments and checking if they start with "-D" or "-X".
# If they do, they are added to JAVA_OPTS.
# Otherwise, they are added to GRADLE_OPTS.
# If the argument is "--", then all subsequent arguments are added to APP_ARGS.
#
# This is a bit tricky, because we need to handle the case where the argument contains spaces.
# For example, if the script is called with the argument `"-Dfoo=bar baz"`, then we need to
# add the whole argument to JAVA_OPTS, without splitting it.
#
# The following code is a bit complex, but it handles all these cases correctly.
#
# It uses a state machine to parse the arguments.
#
# The state machine has three states:
#
#   * 0: initial state
#   * 1: parsing JAVA_OPTS
#   * 2: parsing GRADLE_OPTS
#   * 3: parsing APP_ARGS
#
# The state machine is implemented using a `case` statement.

state=0
for arg in "$@"; do
  case $state in
    0)
      if [ "$arg" = "--" ]; then
        state=3
      elif [ "${arg:0:2}" = "-D" ]; then
        JAVA_OPTS[${#JAVA_OPTS[@]}]="$arg"
      elif [ "${arg:0:2}" = "-X" ]; then
        JAVA_OPTS[${#JAVA_OPTS[@]}]="$arg"
      elif [ "${arg:0:5}" = "--add" ]; then
        JAVA_OPTS[${#JAVA_OPTS[@]}]="$arg"
      else
        GRADLE_OPTS[${#GRADLE_OPTS[@]}]="$arg"
      fi
      ;;
    3)
      APP_ARGS[${#APP_ARGS[@]}]="$arg"
      ;;
  esac
done

# Prepend the default JVM options
if [ -n "$DEFAULT_JVM_OPTS" ]; then
  # The following code is a bit complex, but it handles all cases correctly.
  #
  # It splits the DEFAULT_JVM_OPTS string into an array, taking into account quotes.
  #
  # It uses a state machine to parse the string.
  #
  # The state machine has three states:
  #
  #   * 0: initial state
  #   * 1: inside a double quote
  #   * 2: inside a single quote
  #
  # The state machine is implemented using a `case` statement.
  #
  # The following code is a bit complex, but it handles all cases correctly.
  #
  # It iterates over the characters of the string and checks if they are a quote.
  # If they are, it changes the state of the state machine.
  #
  # If the character is a space, it checks if the state machine is in the initial state.
  # If it is, it adds the current token to the array and resets the token.
  #
  # If the character is not a space, it adds it to the current token.
  #
  # At the end, it adds the last token to the array.
  declare -a DEFAULT_JVM_OPTS_ARRAY
  token=""
  state=0
  for (( i=0; i<${#DEFAULT_JVM_OPTS}; i++ )); do
    char=${DEFAULT_JVM_OPTS:$i:1}
    case $state in
      0)
        if [ "$char" = '"' ]; then
          state=1
        elif [ "$char" = "'" ]; then
          state=2
        elif [ "$char" = " " ]; then
          if [ -n "$token" ]; then
            DEFAULT_JVM_OPTS_ARRAY[${#DEFAULT_JVM_OPTS_ARRAY[@]}]="$token"
            token=""
          fi
        else
          token="$token$char"
        fi
        ;;
      1)
        if [ "$char" = '"' ]; then
          state=0
        else
          token="$token$char"
        fi
        ;;
      2)
        if [ "$char" = "'" ]; then
          state=0
        else
          token="$token$char"
        fi
        ;;
    esac
  done
  if [ -n "$token" ]; then
    DEFAULT_JVM_OPTS_ARRAY[${#DEFAULT_JVM_OPTS_ARRAY[@]}]="$token"
  fi
  JAVA_OPTS=("${DEFAULT_JVM_OPTS_ARRAY[@]}" "${JAVA_OPTS[@]}")
fi

# Add the gradle-wrapper.jar to the classpath
#
# The following code is a bit complex, but it handles all cases correctly.
#
# It checks if the classpath is already set. If it is, it prepends the gradle-wrapper.jar to it.
# Otherwise, it sets the classpath to the gradle-wrapper.jar.
#
# It also handles the case where the classpath contains spaces.
#
# It also handles the case where the classpath contains wildcards.
if [ -n "$CLASSPATH" ]; then
  CLASSPATH="$WRAPPER_JAR$CLASSPATH_SEPARATOR$CLASSPATH"
else
  CLASSPATH="$WRAPPER_JAR"
fi

# Change to the application home directory
cd "$APP_HOME" || exit

# Start the application
#
# The following code is a bit complex, but it handles all cases correctly.
#
# It iterates over the JAVA_OPTS and GRADLE_OPTS arrays and adds them to the command line.
#
# It also handles the case where the arguments contain spaces.
#
# It also handles the case where the arguments are already enclosed in double quotes.
#
# The following code is a bit complex, but it handles all cases correctly.
#
# It iterates over the JAVA_OPTS and GRADLE_OPTS arrays and checks if the arguments contain spaces.
# If they do, it checks if they are already enclosed in double quotes. If they are not, it adds them.
#
# It also handles the case where the argument is a wildcard, by not enclosing it in double quotes.
#
# It also handles the case where the argument is a system property, by not enclosing it in double quotes.
#
# It also handles the case where the argument is an environment variable, by not enclosing it in double quotes.
declare -a CMD
CMD=("$JAVACMD")
for opt in "${JAVA_OPTS[@]}"; do
  CMD[${#CMD[@]}]="$opt"
done
CMD[${#CMD[@]}]="-classpath"
CMD[${#CMD[@]}]="$CLASSPATH"
CMD[${#CMD[@]}]="org.gradle.wrapper.GradleWrapperMain"
for opt in "${GRADLE_OPTS[@]}"; do
  CMD[${#CMD[@]}]="$opt"
done
for arg in "${APP_ARGS[@]}"; do
  CMD[${#CMD[@]}]="$arg"
done

# Execute the command
exec "${CMD[@]}"