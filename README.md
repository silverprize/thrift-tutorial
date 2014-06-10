thrift-tutorial
===============

Apache thrift를 쉽게 접근하기 위한 도움을 제공합니다.<br/>
최신 버전(0.9.1)을 기준으로 합니다.

###### Thrift 설치
- [Download](http://thrift.apache.org/download)
- [Install](http://thrift.apache.org/docs/BuildingFromSource)
- [Trouble shooting](https://docs.google.com/document/d/1vQoibvBzBvCakKxjr_0y9SDKfbG74X_uJtBKVVbNHJ4/edit?usp=sharing)

###### Tutorial 동작 환경
- Ubuntu Linux
- JDK7
- Apache-ant-1.9.4
- Apache-maven-3.2.1
- IntelliJ 13.1.3

###### Tutorial Maven dependencies
    <dependency>
        <groupId>org.apache.thrift</groupId>
        <artifactId>libthrift</artifactId>
        <version>0.9.1</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.5.8</version>
    </dependency>
    
메이븐을 통한 소스코드 자동링크는 현재 버그로 불가능[THRIFT-2269](https://issues.apache.org/jira/browse/THRIFT-2269)<br />
[thrift0.9.1.zip](https://drive.google.com/file/d/0B_EygGt1wSEcTXpsY2wtSWoxRW8/edit?usp=sharing) 다운로드하여 직접 경로지정

###### 모듈 소개
1. interface : Thrift로 생성한 특정 언어의 인터페이스 소스코드를 가지는 모듈로 .thrift파일을 포함하고 있습니다. 
2. client-example : Thrift로 생성된 클라이언트 인터페이스를 구현한 어플리케이션입니다.
3. server-cxample : Thrift로 생성된 서버 인터페이스를 구현한 어플리케이션입니다.
4. common : 외부 라이브러리 묶음 입니다.

###### Example Services
1. echo
2. file upload
3. file download

###### Tutorial
1. Generate source
    - Usage run_thrift
    <pre>$ ./run_thrift [project root] [java or py or ...]</pre>
    - Java
    <pre>$ cd your project path/interface/idl<br/>$ ./run_thrift . java</pre>
    - python
    <pre>$ cd your project path/interface/idl<br/>$ ./run_thrift . py</pre>
2. Prepare common
    - Create Maven artifacts
    <pre>$ cd your project path/common<br/>$ mvn install<br/>$cd ../interface<br/>$ mvn install<br/></pre>
3. Execute server
    - Java
    <pre>$ cd your project path/server-example<br/>$ mvn install<br/>$ java -jar target/server-example-1.0-SNAPSHOT.jar</pre>
4. Execute client
    - Java
    <pre>$ cd your project path/client-example<br/>$ mvn install<br/>$ java -jar target/client-example-1.0-SNAPSHOT.jar</pre>
    - python
    <pre>$cd client-example-py<br/>$ python ExampleClient.py</pre>
    