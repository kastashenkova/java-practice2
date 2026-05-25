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
            <artifactId>Java_Hometask2</artifactId> # або task1/task2
            <version>1.0-SNAPSHOT</version>
        </plugin>
    </plugins>
</build>
```
3. Запустити через PowerShell
- Плагін **collect-source-code**:
  ```
  mvn org.example:task1:1.0-SNAPSHOT:collect-source-code
  ```
  або
  
  ```
  mvn custom-plugin-1:collect-source-code
  ```
- Плагін **comments-generation**:
   1. ```
      mvn org.example:task2:1.0-SNAPSHOT:add-headers -Dauthor="Kateryna Astashenkova"
      ```
      або
      
      ```
      mvn custom-plugin-2:add-headers -Dauthor="Kateryna Astashenkova" # ваше ім'я тут
      ```
   3. ```
      mvn org.example:task2:1.0-SNAPSHOT:generate-comments
      ```
      або
      
      ```
      mvn custom-plugin-2:generate-comments
      ```

## Приклади результатів
- Для завдання 1 результат — згенерований файл `target/collected-source-code.java`, завантажений у репозиторій.
- Для завдання 2 результат — згенеровані коментарі до коду всіх класів проєкту, наявні в репозиторії.
