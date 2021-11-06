if [ -f ~/.bashrc ]; then
	 . ~/.bashrc
fi
export JAVA_HOME="/c/Program Files/Java/jdk1.8.0_162"
PATH="$JAVA_HOME/bin:$PATH:"
parse_git_branch() {
	     git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/ (\1)/'
     }
export PS1="\u@\h \[\033[32m\]\w\[\033[33m\]\$(parse_git_branch)\[\033[00m\] $ "
. ~/.setprompt
