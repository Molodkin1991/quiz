create TABLE question(
  id SERIAL,
  question_content varchar(256),
  topic varchar(256),
  rank int,
  primary key (id)
);

create index question_inx_topic on question(topic);

create TABLE response(
  id SERIAL,
  response_content varchar(256),
  correct BOOLEAN default FALSE,
  question_id INT NOT NULL,
  primary key (id),
  FOREIGN KEY (question_id)
  REFERENCES question(id)
);