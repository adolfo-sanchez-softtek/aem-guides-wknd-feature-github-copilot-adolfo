#!/bin/bash

# Execute bash script with with 1 or 2 params: ./aem-deploy.sh all|core|apps|front e|d|u
# Error (e): -e flag maven provides more verbose error output.
# Debug (d): -X flag enables detailed logs that can help pinpoint the exact cause.
# Update (u): -U flag forces Maven to update the dependencies.
# Samples: ./aem-deploy.sh all u    |    ./aem-deploy.sh core e d   |   ./aem-deploy.sh apps u e    |   ./aem-deploy.sh front

# 1. Delete target folders before deploy
printf "\nDelete target folders!\n"
target_paths=("core/target" "dispatcher/target" "it.tests/target" "ui.apps/target" "ui.apps.structure/target" "ui.config/target" "ui.content/target" "ui.frontend/target" "ui.tests/target")
printf "...\n"
for path in ${target_paths[@]}; do
    if [ -d "$targetpath" ]; then
        sudo rm -rf $targetpath
    fi
done
printf "...\n"
printf "Done!\n\n"

# 2. Define MVN Flags
module=$1
flags=""
str_flags=""
for flag in "$@"; do
	if [ $flag == "u" ] ; then
		flags="${flags}-U "
	elif [ $flag == "e" ]; then
		flags="${flags}-e "
	elif [ $flag == "d" ]; then
        flags="${flags}-X "
    fi
done

if [ ! -z "$flags" ]; then
    if [ $module != "front" ]; then
        str_flags=" with MVN Flag: ${flags}"
    fi
fi

#3. Deploy defined module
printf "\nDeploy ${module}${str_flags}!\n"
if [ $module == "all" ]; then
    mvn $flags clean install -PautoInstallSinglePackage
elif [ $module == "core" ]; then
    mvn $flags -f core/ clean install -PautoInstallBundle
elif [ $module == "apps" ]; then
    mvn $flags -f ui.apps/ clean install -PautoInstallPackage
elif [ $module == "front" ]; then
    npm --prefix ui.frontend/ run dev
fi

printf "\nDone!\n\n"