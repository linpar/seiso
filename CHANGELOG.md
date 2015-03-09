# Change Log

## [0.3.0](https://github.com/ExpediaDotCom/seiso/tree/0.3.0) (2015-03-09)

[Full Changelog](https://github.com/ExpediaDotCom/seiso/compare/0.2.0...0.3.0)

**Implemented enhancements:**

- Remove non-self links from embedded resources [\#82](https://github.com/ExpediaDotCom/seiso/issues/82)

- Add columns to service instance tables [\#79](https://github.com/ExpediaDotCom/seiso/issues/79)

- Machine details page looks dopey while loading [\#78](https://github.com/ExpediaDotCom/seiso/issues/78)

- Data center details page looks dopey while loading [\#77](https://github.com/ExpediaDotCom/seiso/issues/77)

- Decouple loading a data center from loading its service instances and load balancers, and page the collections [\#76](https://github.com/ExpediaDotCom/seiso/issues/76)

- Decouple loading an environment from loading its service instances, and page the collections [\#75](https://github.com/ExpediaDotCom/seiso/issues/75)

- Pages need distinct, sensible titles [\#74](https://github.com/ExpediaDotCom/seiso/issues/74)

- Harmonize the approach to displaying the "page load" message [\#73](https://github.com/ExpediaDotCom/seiso/issues/73)

- Get machine names of a service instance [\#72](https://github.com/ExpediaDotCom/seiso/issues/72)

- Show spinner when paging [\#69](https://github.com/ExpediaDotCom/seiso/issues/69)

- Support for linking service instances to dashboards [\#68](https://github.com/ExpediaDotCom/seiso/issues/68)

- Build Seyren links from DataSource base URI instead of from CustomProperties config [\#67](https://github.com/ExpediaDotCom/seiso/issues/67)

- Add DataSource entity [\#66](https://github.com/ExpediaDotCom/seiso/issues/66)

- Seiso/Seyren integration [\#65](https://github.com/ExpediaDotCom/seiso/issues/65)

- Reinstate sample data [\#64](https://github.com/ExpediaDotCom/seiso/issues/64)

- Improve display of empty item sets [\#63](https://github.com/ExpediaDotCom/seiso/issues/63)

- Global search should include service instances [\#58](https://github.com/ExpediaDotCom/seiso/issues/58)

- Decouple loading service instance and its nodes, and page the nodes. [\#51](https://github.com/ExpediaDotCom/seiso/issues/51)

- Add SQL scripts to find data inconsistencies associated with diamond dependencies [\#49](https://github.com/ExpediaDotCom/seiso/issues/49)

- Limit the size of the global search [\#40](https://github.com/ExpediaDotCom/seiso/issues/40)

- Add per-repo endpoints to get all keys for the repo type and source [\#24](https://github.com/ExpediaDotCom/seiso/issues/24)

**Fixed bugs:**

- Environment details page looks dopey during page load [\#70](https://github.com/ExpediaDotCom/seiso/issues/70)

- RepoSearchDelegate.repoSearch\(\) generates ClassCastException [\#61](https://github.com/ExpediaDotCom/seiso/issues/61)

- Search isn't working [\#57](https://github.com/ExpediaDotCom/seiso/issues/57)

- Rotation state labels are invisible on the service instance details page [\#56](https://github.com/ExpediaDotCom/seiso/issues/56)

- Fix GET and PUT for /v1/endpoints/{id} [\#54](https://github.com/ExpediaDotCom/seiso/issues/54)

- Add port to link \(where it's required\) [\#53](https://github.com/ExpediaDotCom/seiso/issues/53)

- Fix wrong @RestResource paths [\#52](https://github.com/ExpediaDotCom/seiso/issues/52)

- v1 API Accept header should default to application/json [\#48](https://github.com/ExpediaDotCom/seiso/issues/48)

- Moving nodes to new service instance leads to corrupt data [\#33](https://github.com/ExpediaDotCom/seiso/issues/33)

**Merged pull requests:**

- fixed schema and sample data sql [\#60](https://github.com/ExpediaDotCom/seiso/pull/60) ([bradyo](https://github.com/bradyo))

## [0.2.0](https://github.com/ExpediaDotCom/seiso/tree/0.2.0) (2015-02-01)

**Implemented enhancements:**

- Make serviceInstance.loadBalanced an optional field [\#44](https://github.com/ExpediaDotCom/seiso/issues/44)

- Make RabbitMQ exchange names configurable [\#42](https://github.com/ExpediaDotCom/seiso/issues/42)

- Implement v2 PUT item, PUT item property and DELETE requests [\#28](https://github.com/ExpediaDotCom/seiso/issues/28)

- Support ability to import nodes and endpoints config data without overriding state [\#26](https://github.com/ExpediaDotCom/seiso/issues/26)

- Create v2 API based on HAL representations [\#23](https://github.com/ExpediaDotCom/seiso/issues/23)

- Add a source attribute to each importable item [\#15](https://github.com/ExpediaDotCom/seiso/issues/15)

- Upgrade to Spring Boot 1.2.0.RELEASE [\#14](https://github.com/ExpediaDotCom/seiso/issues/14)

- Reactivate change notifications [\#11](https://github.com/ExpediaDotCom/seiso/issues/11)

- Add Vagrant/Chef-based dev environment setup [\#8](https://github.com/ExpediaDotCom/seiso/issues/8)

- Distribute Seiso binaries through jCenter [\#3](https://github.com/ExpediaDotCom/seiso/issues/3)

- Improve formatting of the search engine results page [\#2](https://github.com/ExpediaDotCom/seiso/issues/2)

**Fixed bugs:**

- Unknown column 'endpoints0\_.source\_uri' in 'field list' [\#46](https://github.com/ExpediaDotCom/seiso/issues/46)

- ResourceAssembler puts RotationStatus instance on Resource [\#41](https://github.com/ExpediaDotCom/seiso/issues/41)

- v1 API is returning v2 pagination links [\#39](https://github.com/ExpediaDotCom/seiso/issues/39)

- Getting a v2 list needs to use the HAL representation [\#37](https://github.com/ExpediaDotCom/seiso/issues/37)

- GETting null property /{repo}/{item}/{prop} throws NullPointerException [\#32](https://github.com/ExpediaDotCom/seiso/issues/32)

- Null associations show up as non-embedded properties in HAL representation [\#31](https://github.com/ExpediaDotCom/seiso/issues/31)

- ServiceInstancePortListener generates NullPointerException after PUTting a port [\#30](https://github.com/ExpediaDotCom/seiso/issues/30)

- Repo searches show wrong links [\#25](https://github.com/ExpediaDotCom/seiso/issues/25)

- ItemLinks and PageLinks need to support sort directives [\#22](https://github.com/ExpediaDotCom/seiso/issues/22)

- Seiso API responds with 500 when client tries to import empty item lists [\#16](https://github.com/ExpediaDotCom/seiso/issues/16)

- Home page "loads" forever when there aren't any service groups [\#10](https://github.com/ExpediaDotCom/seiso/issues/10)

- Node details page not showing link to associated machine [\#7](https://github.com/ExpediaDotCom/seiso/issues/7)

**Merged pull requests:**

- Vagrant: Parameterise db "forwarded\_port" settings [\#29](https://github.com/ExpediaDotCom/seiso/pull/29) ([mattcallanan](https://github.com/mattcallanan))

- README & Vagrant tweaks from issues encountered during first-time setup [\#27](https://github.com/ExpediaDotCom/seiso/pull/27) ([mattcallanan](https://github.com/mattcallanan))



\* *This Change Log was automatically generated by [github_changelog_generator](https://github.com/skywinder/Github-Changelog-Generator)*