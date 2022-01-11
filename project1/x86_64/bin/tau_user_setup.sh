#!/bin/sh

# $Id: tau_user_setup.sh.skel,v 1.1 2009/12/01 21:41:25 amorris Exp $

TAUROOT=/home/oscarteeninga/Downloads/tau-2.31
MACHINE=x86_64


if [ ! -d "$TAUROOT" ]; then #If the original root directory is not found find and work from this script's bin directory 

  SOURCE="$0"
  while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    DIR="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
  done
  TAUBIN="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"

  TAUROOT=`dirname $TAUBIN`
  MACHINE=`basename $TAUROOT`
  TAUROOT=`dirname $TAUROOT`

fi #End backup root search


PARAPROF_HOME=${HOME}/.ParaProf
JYTHON_HOME=${HOME}/.ParaProf/jython

if [ ! -d ${PARAPROF_HOME} ] ; then
    mkdir -p ${PARAPROF_HOME}
fi

if [ ! -d ${JYTHON_HOME} ] ; then
    mkdir -p ${JYTHON_HOME}
fi

cat ${TAUROOT}/etc/derby.properties.skel | sed -e 's,@HOME@,'${HOME}',' > ${PARAPROF_HOME}/derby.properties

cp ${TAUROOT}/etc/jython.registry ${JYTHON_HOME}/registry



