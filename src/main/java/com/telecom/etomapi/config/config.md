# Config Package

The `config` package contains the infrastructure and configuration classes required to bootstrap the JAX-RS (Jersey) application and set up Dependency Injection (DI).

## Files

### TroubleTicketApplication.java

This class serves as the entry point for the Jersey application, configuring the resource scanning and registering necessary features.

```java
public class TroubleTicketApplication extends ResourceConfig {
    public TroubleTicketApplication() {
        // Register packages for Resources and ExceptionMappers
        packages("com.telecom.etomapi.controller", "com.telecom.etomapi.exception");
        
        // Register Binder for DI
        register(new TroubleTicketBinder());
        
        // Register Jackson for JSON support
        register(JacksonFeature.class);
    }
}
```

#### Line-by-line Explanation
- **Extends `ResourceConfig`**: Inherits Jersey's configuration capabilities.
- **`packages(...)`**: Scans specified packages for JAX-RS components like controllers and exception mappers.
- **`register(new TroubleTicketBinder())`**: Integrates the HK2 dependency injection configuration.
- **`register(JacksonFeature.class)`**: Enables JSON support using the Jackson library for request/response bodies.

#### Analysis
- **Annotations**: No specific annotations here as it uses programmatic configuration via the constructor.
- **Architectural Decision**: Extending `ResourceConfig` is the idiomatic way to configure a Jersey-based application. It centralizes all registration logic in one place.

---

### TroubleTicketBinder.java

This class defines the dependency injection bindings for the application using HK2.

```java
public class TroubleTicketBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(InMemoryTroubleTicketRepository.class).to(TroubleTicketRepository.class).in(Singleton.class);
        bind(TroubleTicketService.class).to(TroubleTicketService.class).in(Singleton.class);
    }
}
```

#### Line-by-line Explanation
- **Extends `AbstractBinder`**: The base class for defining HK2 bindings.
- **`bind(...).to(...).in(Singleton.class)`**: Maps an implementation to an interface or a class and specifies that only one instance should exist for the application's lifecycle.

#### Analysis
- **Annotations**: 
    - `@Singleton`: Ensures that the repository and service are instantiated only once, which is critical for the `InMemory` repository to maintain state.
- **Architectural Decision**: Decoupling the interface (`TroubleTicketRepository`) from its implementation (`InMemoryTroubleTicketRepository`) via DI allows for easy swapping of the storage layer (e.g., to a database) without changing the service logic.
