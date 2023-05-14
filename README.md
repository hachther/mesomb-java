<h1 align="center">Welcome to java-mesomb 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-1.0.1-blue.svg?cacheSeconds=2592000" />
  <a href="https://mesomb.hachther.com/en/api/v1.1/schema/" target="_blank">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" />
  </a>
  <a href="#" target="_blank">
    <img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-yellow.svg" />
  </a>
  <a href="https://twitter.com/hachther" target="_blank">
    <img alt="Twitter: hachther" src="https://img.shields.io/twitter/follow/hachther.svg?style=social" />
  </a>
</p>

> JAVA client for MeSomb services.
> 
> You can check the full [documentation of the api here](https://mesomb.hachther.com/en/api/v1.1/schema/)

### 🏠 [Homepage](https://mesomb.com)

## Installation

### Gradle

#### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```Gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 2. Add the dependency

```Gradle
dependencies {
    implementation 'com.github.hachther:mesomb-java:TAG'
}
```

### Maven

#### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```XML
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

#### Step 2. Add the dependency

```XML
<dependency>
    <groupId>com.github.hachther</groupId>
    <artifactId>mesomb-java</artifactId>
    <version>Tag</version>
</dependency>
```

## Usage

### Collect money from an account

```JAVA
package com.hachther.mesomb.operations;

import com.hachther.mesomb.util.RandomGenerator;
import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Application;
import com.hachther.mesomb.models.TransactionResponse;
import com.hachther.mesomb.operations.PaymentOperation;

class Test {
    public static void main(String args[]) {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            TransactionResponse response = payment.makeCollect(100, "MTN", "677550203", new Date(), RandomGenerator.nonce());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Depose money in an account

```JAVA
package com.hachther.mesomb.operations;

import com.hachther.mesomb.util.RandomGenerator;
import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Application;
import com.hachther.mesomb.models.TransactionResponse;
import com.hachther.mesomb.operations.PaymentOperation;

class Test {
    public static void main(String args[]) {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            TransactionResponse response = payment.makeDeposit(100, "MTN", "677550203", new Date(), RandomGenerator.nonce());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Get application status

```JAVA
package com.hachther.mesomb.operations;

import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Application;
import com.hachther.mesomb.operations.PaymentOperation;

class Test {
    public static void main(String args[]) {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            Application response = payment.getStatus();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Get transactions by IDs

```JAVA
package com.hachther.mesomb.operations;

import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Application;
import com.hachther.mesomb.operations.PaymentOperation;

class Test {
    public static void main(String args[]) {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            payment.getTransactions(new String[]{"ID1", "ID2"});
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
```


## Author

👤 **Hachther LLC <contact@hachther.com>**

* Website: https://www.hachther.com
* Twitter: [@hachther](https://twitter.com/hachther)
* Github: [@hachther](https://github.com/hachther)
* LinkedIn: [@hachther](https://linkedin.com/in/hachther)

## Show your support

Give a ⭐️ if this project helped you!
