# Sorcery 🧙

[![Pipeline-Status](http://sources.rohenkohl.dev/entwicklung/java/sorcery/badges/main/pipeline.svg)](http://sources.rohenkohl.dev/entwicklung/java/sorcery/-/commits/main) [![Testabdeckung](http://sources.rohenkohl.dev/entwicklung/java/sorcery/badges/main/coverage.svg)](http://sources.rohenkohl.dev/entwicklung/java/sorcery/-/commits/main) [![Neueste Version](http://sources.rohenkohl.dev/entwicklung/java/sorcery/-/badges/release.svg)](http://sources.rohenkohl.dev/entwicklung/java/sorcery/-/releases)

Eine Quarkus-Build-Time-Extension, die `@Dependent`-annotierte, von Hibernate generierte Repositories in ordentliche `@ApplicationScoped` CDI-Beans umwandelt – damit du sie endlich wie ein normaler Mensch mocken kannst.

## 1. Projektbeschreibung

### Was macht diese Extension?

Der Hibernate DAO-Generator versieht Repository-Klassen standardmäßig mit `@Dependent`. Das funktioniert bis du Tests schreiben willst und `@InjectMock` verwendest – dann fliegt dir alles um die Ohren, weil `@Dependent`-Beans in diesem Kontext nicht proxy-fähig und damit nicht mockbar sind.

Diese Extension:

- durchsucht alle Klassen, die auf `Repository` enden
- entfernt die `@Dependent`-Annotation
- ersetzt sie durch `@ApplicationScoped`
- macht das alles zur Build-Zeit von Quarkus, ohne Reflection oder andere Hacks zur Laufzeit

## 2. Verwendung

### Erweiterung in deine Quarkus-Anwendung einbinden

Fertige Pakete werden in der [Container Registry](https://sources.rohenkohl.dev/entwicklung/java/sorcery/-/packages) bereitsgestellt und können im Projekt eingebunden werden:

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>sorcery-deployment</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Bean-Listing aktivieren (optional, aber empfohlen)

```properties
quarkus.arc.list-beans=true
```

Dann kannst du überprüfen, ob deine Repositories jetzt als ApplicationScoped gelistet werden. Verwende dazu @InjectMock in deinen Tests.
Mocking-Probleme gehören der Vergangenheit an. Deine DAOs sind jetzt echte CDI-Beans mit normalem Scope.

### Einschränkungen

Hibernate generiert Implementierungen für Interfaces, indem es den Typnamen mit einem Unterstrich (_) ergänzt.
Diese Extension geht davon aus, dass solche Interfaces auf Repository enden, und wirkt nur auf Implementierungsklassen, deren Namen auf Repository_ enden.

Nur der Scope wird geändert – andere Annotationen bleiben unberührt.

## 3. Beispielnutzung

Füge das in eine deiner Testklassen ein:

```java
@QuarkusTest
class BeanScopeTest {

    @Inject
    CustomerRepository customerRepository;

    @Test
    void checkBeanScope() {
        assertNotNull(customerRepository);
    }
}
```