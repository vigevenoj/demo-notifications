create table earthquakes (
  magnitude float(2), 
  place varchar(128),  
  earthquaketime bigint, 
  updatetime bigint, 
  tz int, 
  url varchar(256), 
  detail varchar(256), 
  felt int, 
  cdi float(2), 
  tsunami int, 
  sig int, 
  code varchar(24), 
  ids varchar(24), 
  type varchar(32), 
  title varchar(256), 
  id varchar(32) primary key,
  longitude decimal(9,6), 
  latitude decimal(9,6)
);

create table locationupdates (
  _type varchar(10), 
  lat decimal(9,6), 
  lon decimal(9,6), 
  acc varchar(8), 
  tst bigint, 
  batt varchar(8), 
  event varchar(32), 
  name varchar(32)
);

create table monitoredlocations (
  latitude decimal(9,6), 
  longitude decimal(9,6), 
  name varchar(32) 
);

create table slacktolocationmapping (
  slackusername varchar(64),
  locationname varchar(64),
  constraint unique_pair primary key(slackusername, locationname)
);
