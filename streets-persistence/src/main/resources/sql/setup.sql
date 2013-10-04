drop table UserGroup;
create table UserGroup
(
  UserGroupID VARCHAR(20) PRIMARY KEY
);

drop table Role;
create table Role
(
  RoleID VARCHAR(20) PRIMARY KEY,
  Description VARCHAR(50),
  Enabled TINYINT(1) NOT NULL DEFAULT 1
);

drop table UserGroupRole;
create table UserGroupRole
(
  UserGroupID VARCHAR(20) NOT NULL REFERENCES UserGroup(UserGroupID) ON UPDATE CASCADE,
  RoleID VARCHAR(20) NOT NULL REFERENCES Role(RoleID) ON UPDATE CASCADE,
  PRIMARY KEY(UserGroupID,RoleID)
);

drop table User;
create table User
(
	UserID BIGINT(19) AUTO_INCREMENT PRIMARY KEY,
	Name VARCHAR(30) NOT NULL,
	UserGroupID VARCHAR(20) NOT NULL REFERENCES UserGroup(UserGroupID) ON UPDATE CASCADE,
	Username VARCHAR(20) NOT NULL,
	Password VARCHAR(256) NOT NULL,
  Status TINYINT(4) NOT NULL,
	Salt VARCHAR(50) NOT NULL,
  LastLoginDate DATETIME
);

drop table Gang;
create table Gang
(
  GangName VARCHAR(30) PRIMARY KEY,
  GangLeaderID BIGINT(19) REFERENCES User(UserID) ON UPDATE CASCADE,
  AIControlled TINYINT(1) NOT NULL DEFAULT 0,
  CurrentBalance DOUBLE NOT NULL,
  Payout DOUBLE NOT NULL
);

drop table UserAttribute;
create table UserAttribute
(
  UserID BIGINT(19) PRIMARY KEY REFERENCES User(UserID) ON UPDATE CASCADE,
  GangName VARCHAR(30) REFERENCES Gang(GangName) ON UPDATE CASCADE,
  BankBalance DOUBLE NOT NULL,
  HealthPoints INTEGER(3) NOT NULL,
  LocationID BIGINT(19) NOT NULL REFERENCES Location(LocationID) ON UPDATE CASCADE
);

drop table Business;
create table Business
(
  BusinessType VARCHAR(30) PRIMARY KEY,
  StartupCost DOUBLE NOT NULL,
  RiskFactor INTEGER(3) NOT NULL,
  Payout DOUBLE NOT NULL
);

drop table Location;
create table Location
(
  LocationID BIGINT(19) PRIMARY KEY,
  LocationName VARCHAR(30) NOT NULL,
  NorthLocationID BIGINT(19) REFERENCES Location(LocationID) ON UPDATE CASCADE,
  SouthLocationID BIGINT(19) REFERENCES Location(LocationID) ON UPDATE CASCADE,
  EastLocationID BIGINT(19) REFERENCES Location(LocationID) ON UPDATE CASCADE,
  WestLocationID BIGINT(19) REFERENCES Location(LocationID) ON UPDATE CASCADE,
  ControllingGangName VARCHAR(30) REFERENCES Gang(GangName) ON UPDATE CASCADE,
  BestBusinessType VARCHAR(30) REFERENCES Business(BusinessType) ON UPDATE CASCADE,
  CurrentBusinessType VARCHAR(30) REFERENCES Business(BusinessType) ON UPDATE CASCADE,
  BusinessProblemID BIGINT(19) REFERENCES Business(BusinessType) ON UPDATE CASCADE
);

drop table Store;
create table Store
(
  StoreID BIGINT(19) PRIMARY KEY AUTO_INCREMENT,
  StoreName VARCHAR(30) NOT NULL,
  LocationID BIGINT(19) NOT NULL REFERENCES Location(LocationID) ON UPDATE CASCADE
);

drop table Item;
create table Item
(
  ItemID BIGINT(19) PRIMARY KEY,
  ItemName VARCHAR (30) NOT NULL,
  ItemDescription VARCHAR (30) NOT NULL,
  ItemValue INTEGER(4) NOT NULL,
  AllowMultiple TINYINT(1) NOT NULL DEFAULT 0
);

drop table StoreItem;
create table StoreItem
(
  StoreID BIGINT(19) NOT NULL REFERENCES Store(StoreID) ON UPDATE CASCADE,
  ItemID BIGINT(19) NOT NULL REFERENCES Item(ItemID) ON UPDATE CASCADE,
  Cost DOUBLE NOT NULL,
  PRIMARY KEY(StoreID,ItemID)
);

drop table UserItem;
create table UserItem
(
  UserID BIGINT(19) NOT NULL REFERENCES User(UserID) ON UPDATE CASCADE,
  ItemID BIGINT(19) NOT NULL REFERENCES Item(ItemID) ON UPDATE CASCADE,
  PRIMARY KEY(UserID,ItemID)
);


drop table BusinessProblem;
create table BusinessProblem
(
  BusinessProblemID BIGINT(19) AUTO_INCREMENT PRIMARY KEY,
  BusinessType VARCHAR(30) REFERENCES Business(BusinessType) ON UPDATE CASCADE,
  ProblemMenuName VARCHAR(30) NOT NULL,
  ProblemDescription VARCHAR(256) NOT NULL,
  Cost DOUBLE NOT NULL
);

-- SETUP DATA
insert into User values (null,'tkaviya','STR_ADMIN','tkaviya','password',1,'salt', null);
insert into UserAttribute values (1,'3-1-2','10000','90', '100');

insert into UserGroup values ('STR_ADMIN');
insert into UserGroupRole values ('STR_ADMIN','ROLE_USER');
insert into UserGroupRole values ('STR_ADMIN','ROLE_ADMIN');

insert into Gang values ('Boyz Dze Smoko',0, 1, 0, 0);
insert into Gang values ('Vitori Brotherhood',0, 1, 0, 0);
insert into Gang values ('3-1-2',1, 0, 0, 0);
insert into Gang values ('Two Sixes',0, 1, 0, 0);

insert into Business values ('Restaurant',100000,1,1000);
insert into Business values ('Casino',5000000,10,5000);
insert into Business values ('Club',1000000,40,3000);
insert into Business values ('Narcotics',30000,80,10000);
insert into Business values ('Weed',2000,70,500);
insert into Business values ('Armed Robbery',5000,90,10000);
insert into Business values ('HiJacking',5000,90,10000);

insert into BusinessProblem values (null,'Restaurant', 'Get A New Chef', 'Your chef has resigned after receiving a better offer elsewhere. You need to hire a new chef immediately to resume business.', 10000);
insert into BusinessProblem values (null,'Casino', 'Repair Damages', 'There was an attempted armed robbery on your casino that resulted in damage to property. You need to repair it to resume business.', 50000);
insert into BusinessProblem values (null,'Club', 'Bribe Cops', 'A fight in your club resulted in a police raid. Several other customers have been found with illegal drugs. You need to pay the cops to keep your license.', 10000);
insert into BusinessProblem values (null,'Club', 'Pay Customer Settlement', 'One of your bouncers severely injured a customer after a misunderstanding. Your licence will be revoked if this gets out. You should settle as fast as possible.', 15000);
insert into BusinessProblem values (null,'Narcotics', 'Bail out customer', 'A customer of yours has been arrested and will rat you out unless you pay for his bail immediately.', 5000);
insert into BusinessProblem values (null,'Narcotics', 'Bail out runner', 'Your drug runner has been apprehended. You need to get him out to continue running your business.', 5000);
insert into BusinessProblem values (null,'Narcotics', 'Send Gift to Chief of Police', 'There\'s a new chief of police in town, and you need to offer him a show of good faith to continue business', 30000);
insert into BusinessProblem values (null,'Weed', 'Bail out customer', 'A customer of yours has been arrested and will rat you out unless you pay for his bail immediately.', 1000);
insert into BusinessProblem values (null,'Armed Robbery', 'Buy new fire arm', 'During a getaway, you lost your fire arm and you need a new one to continue.', 2000);
insert into BusinessProblem values (null,'Armed Robbery', 'Pay bribe', 'Talk of your arrest is underway as your identity has been discovered by a high ranking police officer. An immediate bribe can save you.', 20000);
insert into BusinessProblem values (null,'HiJacking', 'Bail out Henchman', 'Your henchman has been apprehended. You need to get him out to continue running your business.', 2000);

insert into Location values (100, 'Johannesburg', 101, 102, 300, 201, '3-1-2', 'Casino', 'Narcotics', null);
insert into Location values (101, 'Pretoria', 104, 100, 105, 201, 'Two Sixes', null, null, null);
insert into Location values (102, 'Durban', 300, 110, null, 400, null, null, null, null);
insert into Location values (103, 'Messina', 6, 104, null, null, null, null, null, null);
insert into Location values (104, 'Petersburg', 103, 101, 100, null, null, null, null, null);
insert into Location values (105, 'Nelspruit', 103, 300, 500, 101, null, null, null, null);
insert into Location values (106, 'Bloemfontein', 100, 107, 400, null, null, null, null, null);
insert into Location values (107, 'Port Elizabeth', 106, null, 109, 108, null, null, null, null);
insert into Location values (108, 'Cape Town', null, null, 107, null, 'Vitori Brotherhood', 'Club', null, null);
insert into Location values (109, 'East London', 110, null, null, 107, null, null, null, null);
insert into Location values (110, 'Umtata', 400, 109, null, null, null, null, null, null);
insert into Location values (201, 'Gaberone', null, null, null, null, null, null, null, null);
insert into Location values (300, 'Mbabane', 105, 102, 500, 100, null, 'Weed', null, null);
insert into Location values (400, 'Maseru', 100, 110, 102, 106, null, null, null, null);
insert into location values (500, 'Maputo', null, 102, null, 105, null, null, null, null);
insert into Location values (0, 'Harare', 1, 5, 2, 3, null, null, null, null);
insert into Location values (1, 'Kariba', null, 0, null, 3, null, null, null, null);
insert into Location values (2, 'Mutare', null, null, null, 0, null, null, null, null);
insert into Location values (3, 'Victoria Falls', null, 4, 0, null, null, null, null, null);
insert into Location values (4, 'Bulawayo', 3, null, 5, null, null, null, null, null);
insert into Location values (5, 'Masvingo', 0, 6, null, 4, 'Boyz Dze Smoko', null, null, null);
insert into Location values (6, 'BeitBridge', 5, 103, null, null, null, null, null, null);

insert into Store values (1, 'Corner', 100);
insert into Store values (2, 'Corner', 101);
insert into Store values (3, 'Corner', 102);
insert into Store values (4, 'Corner', 103);
insert into Store values (5, 'Corner', 104);
insert into Store values (6, 'Corner', 105);
insert into Store values (7, 'Corner', 106);
insert into Store values (8, 'Corner', 107);
insert into Store values (9, 'Corner', 108);
insert into Store values (10, 'Corner', 109);
insert into Store values (11, 'Corner', 110);
insert into Store values (12, 'Corner', 201);
insert into Store values (13, 'Corner', 300);
insert into Store values (14, 'Corner', 400);
insert into Store values (15, 'Corner', 500);
insert into Store values (16, 'Corner', 0);
insert into Store values (17, 'Corner', 1);
insert into Store values (18, 'Corner', 2);
insert into Store values (19, 'Corner', 3);
insert into Store values (20, 'Corner', 4);
insert into Store values (21, 'Corner', 5);
insert into Store values (22, 'Corner', 6);

insert into Item values (1, 'Pocket Knife', 'Pocket Knife', 50, 0);
insert into Item values (2, 'Semi Automatic Pistol', 'Semi Automatic Pistol', 200, 0);
insert into Item values (3, 'Grenade', 'Grenade', 600, 1);
insert into Item values (4, 'Machine Gun', 'Machine Gun', 500, 0);

insert into StoreItem values (1,1,100.00);
insert into StoreItem values (1,2,10000.00);
insert into StoreItem values (1,3,15000.00);
insert into StoreItem values (1,4,15000.00);

