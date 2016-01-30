CREATE TABLE `HW_UserInfo` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `AdminID` int NOT NULL DEFAULT 0 COMMENT '用户ID',
  `AddTime` datetime NOT NULL COMMENT '创建时间',
  `UpdateTime` datetime NOT NULL COMMENT '修改时间',
  `EmployeeID` varchar(10) NOT NULL DEFAULT '' COMMENT '工号',
  `EmployeeEnName` varchar(16) NOT NULL DEFAULT '' COMMENT '英文姓名',
  `EmployeeCnName` varchar(16) NOT NULL DEFAULT '' COMMENT '中文姓名',
  `EmployeeEmail` varchar(40) NOT NULL DEFAULT '' COMMENT '邮箱',
  `LastLoginTime` datetime NOT NULL COMMENT '最近登录时间',
  `LastLoginIP` varchar(32) NOT NULL DEFAULT '' COMMENT '最近登录IP',
  PRIMARY KEY (`ID`),
  KEY `IX_AdminID` (`AdminID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

CREATE TABLE `HW_UserInfoLog` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `AddTime` datetime NOT NULL COMMENT '创建时间',
  `UpdateTime` datetime NOT NULL COMMENT '修改时间',
  `AdminID` int NOT NULL DEFAULT 0 COMMENT '用户ID',
  `LoginTime` datetime NOT NULL COMMENT '登录时间',
  PRIMARY KEY (`ID`),
  KEY `IX_AdminID` (`AdminID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登录信息表';

CREATE TABLE `HW_UserPersonalization` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `AddTime` datetime NOT NULL COMMENT '创建时间',
  `UpdateTime` datetime NOT NULL COMMENT '修改时间',
  `AdminID` int NOT NULL DEFAULT 0 COMMENT '用户ID',
  `Type` varchar(255) NOT NULL DEFAULT '' COMMENT '类型',
  `Value` varchar(2048) NOT NULL DEFAULT '' COMMENT '取值',
  PRIMARY KEY (`ID`),
  KEY `IX_AdminID_Type` (`AdminID`,`Type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户个性化表';

CREATE TABLE `HW_Query` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `AddTime` datetime NOT NULL COMMENT '创建时间',
  `UpdateTime` datetime NOT NULL COMMENT '修改时间',
  `UUID` char(32) NOT NULL DEFAULT '' COMMENT 'UUID',
  `AdminID` int NOT NULL DEFAULT 0 COMMENT '用户ID',
  `DatabaseName` varchar(32) NOT NULL DEFAULT '' COMMENT '数据库',
  `QueryEngine` varchar(8) NOT NULL DEFAULT '' COMMENT '执行引擎',
  `Role` varchar(32) NOT NULL DEFAULT '' COMMENT '角色',
  `Properties` varchar(2048) NOT NULL DEFAULT '' COMMENT '属性',
  `QuerySql` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '查询语句',
  `QueryStatus` varchar(128) NOT NULL DEFAULT '' COMMENT '状态',
  `ResultFilePath` varchar(2048) NOT NULL DEFAULT '' COMMENT '结果文件路径',
  `Detail` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '详情',
  PRIMARY KEY (`ID`),
  KEY `IX_AdminID` (`AdminID`),
  KEY `IX_UUID` (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='查询信息表';

CREATE TABLE `HW_QueryLog` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `AddTime` datetime NOT NULL COMMENT '创建时间',
  `UpdateTime` datetime NOT NULL COMMENT '修改时间',
  `UUID` char(32) NOT NULL DEFAULT '' COMMENT 'UUID',
  `AdminID` int NOT NULL DEFAULT 0 COMMENT '用户ID',
  `DatabaseName` varchar(32) NOT NULL DEFAULT '' COMMENT '数据库',
  `QueryEngine` varchar(8) NOT NULL DEFAULT '' COMMENT '执行引擎',
  `Role` varchar(32) NOT NULL DEFAULT '' COMMENT '角色',
  `Properties` varchar(2048) NOT NULL DEFAULT '' COMMENT '属性',
  `QuerySql` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '查询语句',
  `QueryStatus` varchar(128) NOT NULL DEFAULT '' COMMENT '状态',
  `ResultFilePath` varchar(2048) NOT NULL DEFAULT '' COMMENT '结果文件路径',
  `Detail` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '详情',
  PRIMARY KEY (`ID`),
  KEY `IX_UUID` (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='查询日志表';

ALTER TABLE `HW_Query`
ADD COLUMN `ExecTime`  int(11) NOT NULL DEFAULT 0 COMMENT '执行时间' AFTER `QuerySql`;

ALTER TABLE `HW_QueryLog`
ADD COLUMN `ExecTime`  int(11) NOT NULL DEFAULT 0 COMMENT '执行时间' AFTER `QuerySql`;

