#!/bin/bash
cd $(dirname "$0")

echo '>> Removing write attribute for read-only files...'
chmod -w ../src/main/java/enshud/Main.java
chmod -w ../src/main/java/enshud/casl/CaslSimulator.java
chmod -w ../src/test/java/enshud/s0/trial/TrialTest.java
chmod -w ../src/test/java/enshud/s1/lexer/LexerTest.java
chmod -w ../src/test/java/enshud/s2/parser/ParserTest.java
chmod -w ../src/test/java/enshud/s3/checker/CheckerTest.java
chmod -w ../src/test/java/enshud/s4/compiler/CompilerTest.java
chmod -w ../build.xml
chmod -w ../data/pas/*
chmod -w ../data/ts/*
chmod -w ../data/ans/*
chmod -w ../data/cas/*

echo '>> Copying pre-commit hook...'
cp pre-commit ../.git/hooks/
chmod +x ../.git/hooks/pre-commit

echo '>> Initializing git default user...'
uid=$(git config -l | grep '09B[0-9]*' -o | head -n 1)
git config --local user.name $uid
git config --local user.email $uid@localhost
git config --local core.ignorecase false


echo '    enshud project successfully initialized!'
