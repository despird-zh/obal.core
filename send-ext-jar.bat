echo off
set "ANT_HOME=D:\c.dev\dev_tool\apache-ant-1.8.3"
set "PATH=PATH;%ANT_HOME%\bin"

echo ANT_HOME is %ANT_HOME%
call ant scp_send_file

pause;
