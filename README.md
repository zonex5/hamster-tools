## Annotation processor and other tools
### Available annotations:

**@DataTransferObject** - generates DTO class
- params: 
  - builder - generate class builder
  - constructor - Generate default constructor
  - destinationPackage - destination package
 
**@NotInclude** - exludes field from generated DTO

**@GenerateRepository** - generates JpaRepository interface for annotated class
- params:
  - EntityIdType (EntityIdType.INTEGER, EntityIdType.LONG)
  - hasActiveFlag - Adds 'getAllByActive' method
  - destinationPackage
- example:
  
```
import java.lang.Integer; 
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
}
```

**@GenerateReactiveRepository** - generates ReactiveCrudRepository interface for annotated class
- params:
  - EntityIdType (EntityIdType.INTEGER, EntityIdType.LONG)
  - hasActiveFlag - Adds 'getAllByActive' method
  - destinationPackage
- example:
  
```
import java.lang.Integer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RoleRepository extends ReactiveCrudRepository<RoleEntity, Integer> {
}
```
