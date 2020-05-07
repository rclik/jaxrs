JAX-RS i RestEasy kullanarak Uygulama
	JAX-RS java nin web service lerinde kullanilmasi icin verilen specification dir.
	Bunun implementation i gerekiyor.
	Bunun icin de en cok bilinen lerinden biri RestEasy, Jersey dir.

Biz RestEasy i kullanicagiz.

Rest Dunyasinda http method lari kullanilir
Bunlar 
	POST PUT GET DELETE HEAD and OPTIONS

		HEAD: GET e benzer ama response body yok. sadece status code ve header bilgileri doner

		OPTIONS: server da hangi method lar desteklendigini dondurmek icin kullanilir.
		
   Bunlarin idempotent ve non-idempotent olmak uzere iki cesitlerdir
	idempotent demek, bir request i birden fazla yapsak da sonuc server tarafinda ayni olmasidir.
	bu kapsamda PUT, GET, DELETE metthod lari idempotent dir, cunku ne kadar o request atilirsa atilsin server tarafinda response aynidir.
	sadece POST non-idempotent dir.

Rest Dunyasinda server da resource lar vardir ve REST bunlara ulasilamasini ve yonetilmesini saglar,
Bu resource lar addressable olmasi gerekiyor, yani server uzerindeki bir resource a direk ulasilabilmeli. Yani bir unique identifier ile o resource a ulasilabilmelidir. Buna da URI denir. (Uniform Resource Identifer)
	/employee/1234 gibi

REST stateless dir, 

HATEOS (Hypermedia As The Engine Of Application State)
    bu ise resource uzerinde hangi islemler yapilabilir bilgisini donulmesini saglayan bir uygulamadir,
    soyle dusun, bir resource request ettin, server ise o resource u ve uzerinde yapilabilecek islemleri sana donuyor. 
    bu islemleri donmesinin sebebi application in HATEOAS i desteklemesidir.
    
RestEasy Kullanarak JAX-RS Uygulamasinin Yapilmasi:
    RestEasy bir JAX-RS uygulamasi, yoksa JAX-RS sadece bir kosullar butunudur. Interface gibi dusunebilirsin.
    RestEasy icin pom.xml le sunlari eklemek lazim:
    ...
      <properties>
      	<resteasy.version>3.6.2.Final</resteasy.version>
      </properties>
      <dependencies>
      	<dependency>
    		<groupId>org.jboss.resteasy</groupId>
    		<artifactId>resteasy-jaxrs</artifactId>
    		<version>${resteasy.version}</version>
    	</dependency>
      </dependencies>
    ...
    
Root Resource Classes
    bunlar ise POJO lardir (plain old java object)
    bu class public olmalidir ve constructor i private olmamalidir. yoksa JAX-RS uygulamasi tarafindan runtime da initiate edilemez.
    bu class larin basina @Path annotation i konur ve URI deki resource kismina karsilik gelir.
    
    @Path("/messages")
    public class RestMessageController {
    ...
    }
   
   Her request geldiginde, JAX-RS tarafindan root resource object i create edilir, constructor cagrilir, dependency leri varsa inject edilir. 
   Islem de bitince object sonlandirilir ve GC tarafindan silinmeye birakilir.
   
   Soru: JAX-RS resource u nasil bulacak? Bunun farkli yontemleri var, ilk olarak 
   
Resource Methods
    Resource lara gelen http method larini karsilayan method lardir. 
    bunlar public olmalidir.
    checked ve unchecked exception atabilirler
    donus tipleri void Response olabilir
    
Root Resource Class ve Resource Method la ornek class:

    @Path("/messages")
    public class RestMessageController {
        @Path("/message")
        public String getMessage() {
            return "Hello World!";
        }
    }
Bu kadar. Simdi ise soru su: bu root resource u JAX-RS uygulamasina nasil tanitacagiz?
Genel olarak iki cozumu var; 
    - web.xml kullanarak:
    - annotation kullanarak:
    java web deployment descriptor JavaEE standard larina gore belirlenir. 2.5 den once sinde web.xml java web uygulamalarinda olmalidir.
    sonraki versiyonlarinda ise java servlet container i ilgili annotation lari search ederek onlari context ine ekler. yani web.xml e gerek kalmaz.
    
 Ilk olarak web deployment descriptor araciligiyla root resource u eklemeyi yapalim:
    - ilk olarak javax.ws.rs.core.Application class ini extend eden bir class yazalim. Bu class bizim application imizi temsil edecek ve servlet container
    ayaga kalkarken bunrada insert ettigimiz resource lari veya interceptor lari ayaga kaldiracak.
    bu class da singletons dedigimiz application nin resource larini tutacak bir datastructure objesi olur, resource larimizi ona register ederiz. 
    Sonrada da getSingletons() method unu override ederek, return olarak de resource data object ini doneriz.
    
    public class RestMessageApplication extends Application {
        private Set<Object> singletons = new HashSet<Object>();
    
        public RestMessageApplication(){
            singletons.add(new RestMessageController());
        }
    
        @Override
        public Set<Object> getSingletons() {
            return singletons;
        }
    }
    
Root resource lar singleton veya request scope olabililer. 
    singleton yapmak icin, getSingletons method u override edilmelidir.
    per request yapmak icin getClasses() method u override edilmelidir.


bundan sonra ise bu class i web container i gostermemiz gerekiyor. o isi de web.xml de yapiyoruz.
web.xml de gostermenin de iki yolu var:
    - servlet description i ile
    - filter description i ile.
ikisindeki mantik da aynidir. birinde servlet ayaga kalkarken application register edilir. digerinde ise filter ayaga kalkarken application register edilir.

Servlet Ayaga Kalkarken:
    - Ayaga kalkan servlet RestEasy nin servleti org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher 
    - Bu servlet definition ini web.xml de veriyoruz ve init-param olarak da application i veriyoruz:
    
    <web-app xmlns="http://java.sun.com/xml/ns/javaee"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
              http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
             version="2.5">
        <display-name>injavawetrust.resteasy</display-name>
        <servlet>
            <servlet-name>HttpServletDispatcher</servlet-name>
            <servlet-class>
                org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher
            </servlet-class>
            <init-param>
                <param-name>javax.ws.rs.core.Application</param-name>
                <param-value>com.rcelik.jaxrs.restmessage.application.RegisterApplication</param-value>
            </init-param>
        </servlet>
        <servlet-mapping>
            <servlet-name>HttpServletDispatcher</servlet-name>
            <url-pattern>/*</url-pattern>
        </servlet-mapping>
    </web-app>
    
servlet mapping ini de yapmayi unutmamak lazim.

Application root context ini ise Intellij den verebiliyorsun. Default olarak verebilemek icin kullandigin application server a gore kurallar var. onlara gore verebiliyorsun.

intellij den verdigimiz application context i myapp olsun. o zaman bizim root resource icin verecegimiz url su sekilde olur:
    localhost:8080/myapp/messages/message
    
simdi ise url mapping yaptigmiz yere /* den farkli bir deger verelim: mesela /rest/* 
bunun calistirilmasi icin context-param degerinin de verilmesi gerekiyor:

    <servlet-mapping>
        <servlet-name>HttpServletDispatcher</servlet-name>
        <url-pattern>/rest/*</url-pattern>
    </servlet-mapping>

    <context-param>
        <param-name>resteasy.servlet.mapping.prefix</param-name>
        <param-value>/rest</param-value>
    </context-param>
   
Burada oldugu gibi. 


Filter ile yapilabilir. tam olarak ayni mantik ama bu kere RestEasy nin servlet i yerine filter i ayaga kalkarken application i bind edecegiz.

     <filter>
        <filter-name>FilterDispatcher</filter-name>
        <filter-class>
            org.jboss.resteasy.plugins.server.servlet.FilterDispatcher
        </filter-class>
        <init-param>
            <param-name>javax.ws.rs.Application</param-name>
            <param-value>service.RegisterApplication</param-value>
        </init-param>
    </filter>
     <filter-mapping>
        <filter-name>FilterDispatcher</filter-name>
        <url-pattern>/restfilter/*</url-pattern>
    </filter-mapping>
    <context-param>
        <param-name>resteasy.servlet.mapping.prefix</param-name>
        <param-value>/restfilter</param-value>
    </context-param>
    
tamamen ayni sadece filter ve filter in ismi: org.jboss.resteasy.plugins.server.servlet.FilterDispatcher

Bu yaptiklarimizi Servlet 3 api ile yapalim bir de, yani xml den bagimsiz olarak. Onun icin ise yeni projeye gecebiliriz.





































