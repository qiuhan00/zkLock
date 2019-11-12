CREATE TABLE `tbl_product` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `productname` varchar(20) DEFAULT NULL,
  `producttype` varchar(20) DEFAULT NULL,
  `productnumber` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `tbl_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productname` varchar(20) DEFAULT NULL,
  `username` varchar(20) DEFAULT NULL,
  `productnumber` int(10) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;

