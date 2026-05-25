# Практика 2. Maven
## Завдання
1. Створити власний плагін і використати його в проєкті. Плагін повинен зібрати весь вихідний java код в один файл.
> [https://maven.apache.org/guides/plugin/guide-java-plugin-development.html](https://maven.apache.org/guides/plugin/guide-java-plugin-development.html)
2. Створити власний плагін і використати його в проєкті. Тема проєкту вільна.

Обрана тема: **система генерування коментарів**.

## Додавання плагінів в інших проєктах
1. Встановити плагін локально: `mvn clean install`
2. Додати у файл конфігурації `pom.xml` відповідний плагін:
```
<build>
    <plugins>
        <plugin>
            <groupId>org.example</groupId>
            <artifactId>Java_Hometask2</artifactId>
            <version>1.0-SNAPSHOT</version>
        </plugin>
    </plugins>
</build>
```
3. Запустити через PowerShell
- Плагін **collect-source-code**:
  1. `mvn org.example:Java_Hometask2:1.0-SNAPSHOT:collect-source-code`
  2. `mvn custom-plugin:collect-source-code`
- Плагін **comments-generation**:
   1. `mvn org.example:Java_Hometask2:1.0-SNAPSHOT:comments-generation`
   2. `mvn custom-plugin:comments-generation`
