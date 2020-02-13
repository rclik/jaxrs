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
    sonraki versiyonlarinda ise java servlet container i ilgili annotation lari search ederek onlari context ine ekler.
    
