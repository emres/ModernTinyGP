# ModernTinyGP

ModernTinyGP is the **modernized** version of TinyGP, the genetic programming
software described in
"[A Field Guide to Genetic Programming](http://www.gp-field-guide.org.uk/)"
(ISBN 978-1-4092-0073-4) by Riccardo Poli, Bill Langdon, Nic McPhee, published
in March, 2008 (See also its [Amazon.com
page](https://www.amazon.com/Field-Guide-Genetic-Programming/dp/1409200736)).

The original code is in https://github.com/emres/TinyGP. The algorithm and 
implementation is almost the same except for a few Java refactorings and
[Apache Maven](https://maven.apache.org/)-based build system.

The book was published in **March, 2008**, when **Java Standard Edition 6** was the
current Java version. Now, in **January 2024**, almost 16 years later, 
**Java SE 21 (Long Term Support)** is the current Java version
(see [Java version history](https://en.wikipedia.org/wiki/Java_version_history)
for more details).

This repository is intended as a **playground** and **experimentation** area
based on the original TinyGP project.

You can build the project by using the following [Maven](https://maven.apache.org/)
command line:
```
$ mvn package
```

As described in the book, if the dataset is stored in a file `problem.dat`, the
program can be launched with the following command:

```
$ java -cp target/modern-tiny-gp-1.0-SNAPSHOT.jar be.tmdata.ModernTinyGP.ModernTinyGP
```

Otherwise, the user can specify a different datafile on the command line, by
giving the command

```
$ java -cp target/modern-tiny-gp-1.0-SNAPSHOT.jar be.tmdata.ModernTinyGP.ModernTinyGP FILE
```

where `FILE` is the dataset file name (which can include the full path to the
file). Finally, the user can specify both the datafile and a seed for the random
number generator on the command line, by giving the command:

```
$ java -cp target/modern-tiny-gp-1.0-SNAPSHOT.jar be.tmdata.ModernTinyGP.ModernTinyGP SEED FILE
```

where `SEED` is an integer.

For convenience, the example data file is incuded in this repository as
`problem.dat`. It is identical to `sin-data.txt`.

## Other relevant repositories on GitHub

There are also a few other repositories on GitHub that have the original source
code, as well as attempts at Java modernisation:

* https://github.com/JesseBuesking/TinyGP-Java
* https://github.com/marcinkalaus/TinyGP-Java
* https://github.com/Pandoors/TinyGP
* https://github.com/Sitaarz/TinyGP-Java
* https://github.com/tlisowicz/TinyGP-Java