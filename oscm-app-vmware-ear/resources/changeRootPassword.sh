#!/bin/sh

# Change root password (CentOS 6.2)

# After a new VM is created and reconfigured this script is transfered
# to the new VM and will be executed with the credentials that are defined
# in SCRIPT_USERID and SCRIPT_PWD.
# The root password is taken from the service parameter LINUX_ROOT_PWD
# All service parameters are inserted as variables into the script 
# before it is transfered to the VM.

echo `date` "Change root password."
echo "root:$LINUX_ROOT_PWD" | chpasswd
exit $?;

