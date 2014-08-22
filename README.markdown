# dynjs - ECMAScript Runtime for the JVM

[![Build Status](https://secure.travis-ci.org/dynjs/dynjs.png)](http://travis-ci.org/dynjs/dynjs)

![cloudbees rocks!](http://static-www.cloudbees.com/images/badges/BuiltOnDEV.png)

## Bug Reports

We're using [GitHub Issues](https://github.com/dynjs/dynjs/issues/). Please let us know what issues you find!

## Setting up your environment


### Getting JDK7

You can get information on installing JDK7 at the [java site](http://www.java.com/en/download/faq/java_mac.xml).

1. Download and install it to your user (not to the entire machine)
2. Before running `mvn install` run the following to set the JDK7 as your default compiler

```shell		
export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)
```

If you run into problems with this when working on OSX (due to the default Apple JDK) run the following command to make sure the JDK7 installed properly:

```shell
$ /usr/libexec/java_home -V
Matching Java Virtual Machines (4):
    1.8.0, x86_64:  "Java SE 8" /Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home
    1.7.0_67, x86_64:   "Java SE 7" /Library/Java/JavaVirtualMachines/jdk1.7.0_67.jdk/Contents/Home
    1.6.0_65-b14-462, x86_64:	"Java SE 6"	/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
    1.6.0_65-b14-462, i386:	"Java SE 6"	/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
```

If you're missing the line starting `1.7.0` then the JDK7 didn't install properly.

## Getting started


### Building from sources

1. `git clone https://github.com/dynjs/dynjs.git && cd dynjs`
2. `mvn install -s support/settings.xml`

### Download

Alternatively download the [latest dynjs dist zip package](https://projectodd.ci.cloudbees.com/job/dynjs-snapshot/lastSuccessfulBuild/artifact/target/) from our CI job, then unpack it somewhere. As a convenience, you can symlink `bin/dynjs` to some directory listed on your `$PATH`.

### Running 

Run `./bin/dynjs --console` for the REPL and try the snippet below:

```javascript
var x = 1 + 1;
print(x);
```

For more options, run `./dynjs --help`.

