create table account
(
    id      uuid         not null,
    name    varchar(200) not null,
    icon_id uuid         not null,
    user_id uuid         not null,
    primary key (id)
);
comment
    on table account is 'Счёт';
comment
    on column account.id is 'ID';
comment
    on column account.name is 'Наименование';
comment
    on column account.icon_id is 'ID колонки';
comment
    on column account.user_id is 'ID пользователя';

create table category
(
    id        uuid         not null,
    income    bool         not null,
    name      varchar(400) not null,
    icon_id   uuid         not null,
    parent_id uuid,
    primary key (id)
);
comment
    on table category is 'Категория';
comment
    on column category.id is 'ID';
comment
    on column category.income is 'Доход';
comment
    on column category.name is 'Наименование';
comment
    on column category.icon_id is 'ID иконки';
comment
    on column category.parent_id is 'ID родителя';

create table icon
(
    id      uuid  not null,
    icon    bytea not null,
    tooltip varchar(200),
    primary key (id)
);
comment
    on table icon is 'Иконка';
comment
    on column icon.id is 'ID';
comment
    on column icon.icon is 'Иконка';
comment
    on column icon.tooltip is 'Тултип';

create table operation
(
    id          uuid           not null,
    user_id     uuid           not null,
    category_id uuid           not null,
    amount      numeric(10, 2) not null,
    "date"      date           not null,
    description varchar(1000),
    primary key (id)
);
comment
    on table operation is 'Операция';
comment
    on column operation.id is 'ID';
comment
    on column operation.user_id is 'ID пользователя';
comment
    on column operation.category_id is 'ID категории';
comment
    on column operation.amount is 'Сумма';
comment
    on column operation."date" is 'Дата';
comment
    on column operation.description is 'Описание';

create table user_question
(
    id       uuid          not null,
    question varchar(1000) not null unique,
    primary key (id)
);
comment
    on table user_question is 'Секретный вопрос';
comment
    on column user_question.id is 'ID';
comment
    on column user_question.question is 'Вопрос';

create table usr
(
    id              uuid         not null,
    login           varchar(100) not null,
    password        varchar(200) not null,
    role            varchar(100) not null,
    question_id     uuid         not null,
    question_answer varchar(200) not null,
    primary key (id)
);
comment
    on table usr is 'Пользователь';
comment
    on column usr.id is 'ID';
comment
    on column usr.login is 'Логин';
comment
    on column usr.password is 'Пароль (зашифровано)';
comment
    on column usr.role is 'Роль';
comment
    on column usr.question_id is 'ID секретного вопроса';
comment
    on column usr.question_answer is 'Ответ на секретный вопрос (зашифровано)';

create unique index account_id
    on account (id);
create index account_icon_id
    on account (icon_id);
create index account_user_id
    on account (user_id);
create unique index category_id
    on category (id);
create index category_income
    on category (income);
create unique index icon_id
    on icon (id);
create unique index operation_id
    on operation (id);
create index operation_user_id
    on operation (user_id);
create index operation_category_id
    on operation (category_id);
create unique index user_question_id
    on user_question (id);
create unique index usr_id
    on usr (id);
create unique index usr_login
    on usr (login);
create index usr_password
    on usr (password);
create index usr_role
    on usr (role);
create index usr_question_id
    on usr (question_id);

alter table operation
    add constraint fk_category_operation foreign key (category_id) references category (id);
alter table category
    add constraint fk_category_parent_category foreign key (parent_id) references category (id);
alter table account
    add constraint fk_icon_account foreign key (icon_id) references icon (id);
alter table category
    add constraint fk_icon_category foreign key (icon_id) references icon (id);
alter table account
    add constraint fk_user_account foreign key (user_id) references usr (id);
alter table operation
    add constraint fk_user_operation foreign key (user_id) references usr (id);
alter table usr
    add constraint fk_user_user_question foreign key (question_id) references user_question (id);