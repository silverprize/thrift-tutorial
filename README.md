thrift-tutorial
===============

Apache thrift를 쉽게 접근하기 위한 도움을 제공합니다.<br />
최신 버전(0.9.1)을 기준으로 합니다.

###### 설치
- [Download](http://thrift.apache.org/download)
- [Install](http://thrift.apache.org/docs/BuildingFromSource)
- [Trouble shooting](https://docs.google.com/document/d/1vQoibvBzBvCakKxjr_0y9SDKfbG74X_uJtBKVVbNHJ4/edit?usp=sharing)

###### 개발환경
- Ubuntu Linux
- JDK7
- Apache-ant-1.9.4
- Apache-maven-3.2.1
- IntelliJ 13.1.3

###### 필수 Maven dependencies
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
[thrift0.9.1.zip](https://drive.google.com/file/d/0B_EygGt1wSEcTXpsY2wtSWoxRW8/edit?usp=sharing) 다운로드하여 직접 경로지정 필요


###### 예제
- Echo service
- File upload/download service 
