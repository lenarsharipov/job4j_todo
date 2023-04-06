## Проект "Приложение TODO LIST - Список заданий"

Web-приложение "TODO LIST - Список заданий".
Позволяет добавлять, показывать, редактировать, удалять задания.
В таблице отображается имя, дата создания и состояние (выполнено или нет).

____________________________________________
<h3>Стек технологий:</h3>
<ul>
    <li>Java 17</li>
    <li>Postgresql 14</li>
    <li>Spring Boot 2.7.3</li>
    <li>Hibernate 5.6.11</li>
    <li>Lombok 1.18.22</li>
    <li>Bootstrap 5.2.3</li>
    <li>Liquibase 4.15.0</li>
    <li>Checkstyle 8.42</li>
    <li>Junit5</li>
    <li>Assertj</li>
    <li>H2 2.1.214</li> 
    <li>Mockito 4.0.0</li> 
    <li>Jacoco 0.8.8</li> 
    <li>Jcip-annotations 1.0</li>
    <li>Log4j 1.2.17</li>
    <li>Slf4j 1.7.30</li>
</ul>

____________________________________________
<h3>Окружение:</h3>
Java 17, Maven 3.8.7, Postgresql 14

____________________________________________
<h3>Запуск проекта:</h3>
<ol>
    <li>Создать базу данных <b>todo</b>. Пользователь <b>postgres</b> c паролем <b>password</b>:</li>
    <pre>create database todo;</pre>
    <li>Скачать zip-архив программы:</li>
    <pre><img src="/src/main/resources/static/img/readme/screenshots/zip_archive_git.png" title="Download Zip-archive"/></pre>
    <li>Распакуйте архив, откройте в качестве проекта папку в <b>Intellij Idea</b></li>
    <li>Во вкладке <b>Maven</b> запустите поочередно команды <b>clean, test</b></li>
    <li>Откройте класс <b>Main.class</b> и запустите его.</li>
    <li>В адресной строке браузера введите <b>localhost:8080</b> либо <b>localhost:8080/index</b></li>
</ol>

____________________________________________
<h3>Покрытие тестами:</h3>
<img src="/src/main/resources/static/img/readme/screenshots/jacoco.png" title="Jacoco"/>

____________________________________________
<h3>Взаимодействие с приложением:</h3>
<p>Главная страница</p>
<img src="/src/main/resources/static/img/readme/screenshots/home.png" title="Home page"/>
<br>
<p>Все задачи</p>
<img src="/src/main/resources/static/img/readme/screenshots/all_tasks.png" title="All tasks page"/>
<br>
<p>Выполненные задачи</p>
<img src="/src/main/resources/static/img/readme/screenshots/completed_tasks.png" title="Completed tasks page"/>
<br>
<p>Новые задачи</p>
<img src="/src/main/resources/static/img/readme/screenshots/new_tasks.png" title="New tasks page"/>
<br>
<p>Создать задачу</p>
<img src="/src/main/resources/static/img/readme/screenshots/add_task.png" title="Add new task page"/>
<br>
<p>Детальная страница задачи</p>
<img src="/src/main/resources/static/img/readme/screenshots/task_detailed_info.png" title="Task info"/>
<br>
<p>Отредактировать задачу</p>
<img src="/src/main/resources/static/img/readme/screenshots/task_edit.png" title="Task edit page"/>
<br>
<p>Страница аутентификации</p>
<img src="/src/main/resources/static/img/readme/screenshots/login_page.png" title="Login page"/>
<br>
<p>Пользователь аутентифицирован</p>
<img src="/src/main/resources/static/img/readme/screenshots/loggedin.png" title="User logged in"/>
<br>
<p>Ошибка</p>
<img src="/src/main/resources/static/img/readme/screenshots/login_error.png" title="Login error"/>
<br>

____________________________________________
<h3>Контакты:</h3>
<ul>
    <li><a href="mailto:lenarsharipov@gmail.com">lenarsharipov@gmail.com</a></li>
    <li><a href="https://t.me/LenarSharipov">telegram</a></li>
</ul>