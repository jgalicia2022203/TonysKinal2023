/*
Name: Juan Luis Galicia Mazariegos
Technical code: IN5BV
ID: 2022203
Creation Date: 30-03-2023 4:48 P.M. 
Modification Date:  30-03-2023 08:05 P.M. - 10:05 P.M.
					04-04-2023 03:50 P.M. - 10:13 P.M.
                    05-05-2023 07:36 P.M. - 10:33 P.M.
                    08-05-2023 08:33 P.M. - 09:10 P.M.
                    02-06-2023 13:00 P.M. - 13:11 P.M.
*/

-- ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'admin';
-- flush privileges;
-- SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));

Drop database if exists DBTonysKinal2023;
Create database DBTonysKinal2023;

Use DBTonysKinal2023;

Create table Companys(
	codeCompany int auto_increment not null,
    nameCompany varchar(150) not null,
    adress varchar(150) not null,
    phone varchar(10) not null,
    primary key PK_codeCompany (codeCompany)
);

Create table TypeEmployees(
	codeTypeEmployee int not null auto_increment,
    descript varchar(50) not null,
    primary key PK_codeTypeEmployee (codeTypeEmployee)
);

Create table Employees(
	codeEmployee int auto_increment not null,
    numberEmployee int not null,
    secondNameEmployee varchar(150) not null,
    firstNameEmployee varchar(150) not null,
    adressEmployee varchar(150) not null,
    contactPhone varchar(10) not null,
    cookDegree varchar(50) not null,
    codeTypeEmployee int not null,
    primary key PK_codeEmployee (codeEmployee),
    constraint FK_Employees_TypeEmployees foreign key
		(codeTypeEmployee) references TypeEmployees(codeTypeEmployee) ON DELETE CASCADE
);

Create table TypeDish(
	codeTypeDish int auto_increment not null,
    descriptionType varchar(100) not null,
    primary key PK_codeTypeDish (codeTypeDish)
);

Create table Products(
	codeProduct int not null,
    nameProduct varchar(150) not null,
    quantity int not null,
    primary key PK_codeProduct (codeProduct)
); 

Create table Services(
	codeService int auto_increment not null,
    dateService date not null,
    typeService varchar(150) not null,
    hourService time not null,
    placeService varchar(150) not null,
    phoneContact varchar(10) not null,
    codeCompany int not null,
    primary key PK_codeService (codeService),
    constraint FK_Services_Companys foreign key 
		(codeCompany) references Companys(codeCompany) ON DELETE CASCADE
);

Create table Budgets(
	codeBudget int auto_increment not null,
    dateRequest date not null,
    amountBudget decimal(10,2) not null,
    codeCompany int not null,
    primary key PK_codeBudget (codeBudget),
    constraint FK_Budgets_Companys foreign key
		(codeCompany) references Companys(codeCompany) ON DELETE CASCADE
);

Create table Dishes(
	codeDish int auto_increment not null,
    quantity int not null,
    nameDish varchar(50) not null,
    descriptionDish varchar(150) not null,
    priceDish decimal (10,2) not null,
    codeTypeDish int not null,
    primary key PK_codeDish (codeDish),
    constraint FK_Dishes_TypeDish foreign key 
		(codeTypeDish) references TypeDish(codeTypeDish) ON DELETE CASCADE
);

Create table Products_has_Dishes(
	Products_codeProduct int not null,
    codeDish int not null,
    codeProduct int not null,
    primary key PK_Products_codeProduct(Products_codeProduct),
    constraint FK_Products_has_Dishes_Product1 foreign key 
		(codeProduct) references Products(codeProduct) ON DELETE CASCADE,
	constraint FK_Products_has_Dishes_Dishes1 foreign key
		(codeDish) references Dishes(codeDish) ON DELETE CASCADE
);

Create table Services_has_Dishes(
	Services_codeService int not null,
    codeDish int not null,
    codeService int not null,
    primary key PK_Services_codeService(Services_codeService),
    constraint FK_Services_has_Dishes_Services1 foreign key
		(codeService) references Services(codeService) ON DELETE CASCADE,
	constraint FK_Services_has_Dishes_Dishes1 foreign key
		(codeDish) references Dishes(codeDish) ON DELETE CASCADE
);

Create table Services_has_Employees(
	Services_codeService int not null,
    codeService int not null,
    codeEmployee int not null,
    dateEvent date not null,
    hourEvent time not null,
    placeEvent varchar(150) not null,
    primary key PK_Services_codeService (Services_codeService),
    constraint FK_Services_has_Employees_Services1 foreign key
		(codeService) references Services(codeService) ON DELETE CASCADE,
	constraint FK_Services_has_Employees_Employees1 foreign key
		(codeEmployee) references Employees(codeEmployee) ON DELETE CASCADE
);

Create table Users(
	codeUser int auto_increment not null,
    firstNameUser varchar(100) not null,
    secondNameUser varchar(100) not null,
    userLogin varchar(50) not null,
    passwordUser varchar(50) not null,
    primary key PK_codeUser (codeUser)
);

Create table Login(
	userMaster varchar(50) not null,
    passwordLogin varchar(50) not null,
    primary key PK_userMaster (userMaster)
);

-- Use DBRecovery;

-- ------------------- Storage Procedures Entity Users -- -------------------

-- ------------------- Create User -- -------------------
Delimiter $$
	Create procedure sp_CreateUser(in firstNameUser varchar(100), in secondNameUser varchar(100), 
								   in userLogin varchar(50), in passwordUser varchar(50))
		Begin 
			Insert into Users(firstNameUser, secondNameUser, userLogin, passwordUser)
				values(firstNameUser, secondNameUser, userLogin, passwordUser);
        End$$
Delimiter ; 

call sp_CreateUser('Juan', 'Galicia', 'Tonelito', 'admin');
-- ------------------- Read Users -- -------------------
Delimiter $$
	Create procedure sp_ReadUsers()
		Begin 
			Select U.codeUser,
				   U.firstNameUser,
				   U.secondNameUser,
                   U.userLogin,
                   U.passwordUser from Users U;
        End$$
Delimiter ;

call sp_ReadUsers();

-- ------------------- Storage Procedures Entity Companys -- -------------------

-- ------------------- Create Company -- -------------------
Delimiter $$
	Create procedure sp_CreateCompany(in nameCompany varchar(150), in adress varchar(150), phone varchar(10))
    Begin 
		Insert into Companys(nameCompany, adress, phone)
			values(nameCompany, adress, phone);
    End$$
Delimiter ;

call sp_CreateCompany('Fresh Flavors Co.', '987 Grape St. Suite 300 Miami', '305-3434');
call sp_CreateCompany('Savory Bites', '654 Orange St. Suite 200 Miami', '305-2323');
call sp_CreateCompany('Delicius Delights', '321 Cherry Ave. Suite Miami ' ,'305-1212');

-- ------------------- Read Companys -- -------------------
Delimiter $$
	Create procedure sp_ReadCompanys()
    Begin
		Select C.codeCompany,
				C.nameCompany,
				C.adress,
                C.phone from Companys C;
	End$$
Delimiter ;

call sp_ReadCompanys();

-- ------------------- Update Company -- -------------------
Delimiter $$
	Create procedure sp_UpdateCompany(in cCompany int, in nameCompa varchar(150), in ad varchar(150), in ph varchar(10))
    Begin
		Start transaction;
			Update Companys C 
				set C.nameCompany = nameCompa,
					C.adress = ad,
					C.phone = ph
                where C.codeCompany = cCompany;
		commit;
    End$$
Delimiter ;

-- ------------------- Delete Company -- -------------------
Delimiter $$
	Create procedure sp_DeleteCompany(in cCompany int)
    Begin
		Delete from Companys 
			where codeCompany = cCompany;
    End$$
Delimiter ;

-- ------------------- Search Company -- -------------------
Delimiter $$
	Create procedure sp_SearchCompany(in cCompany int)
    Begin
		Select 	C.codeCompany,
				C.nameCompany,
				C.adress,
                C.phone from Companys C
                where C.codeCompany = cCompany;
    End$$
Delimiter ;
-- ------------------- Storage Procedures Entity TypeEmployees -- -------------------

-- ------------------- Create TypeEmployee -- -------------------
Delimiter $$
	Create procedure sp_CreateTypeEmployee(in descript varchar(50))
    Begin
		Insert into TypeEmployees(descript)
			values(descript);
    End$$
Delimiter ;

call sp_CreateTypeEmployee('Chef');
call sp_CreateTypeEmployee('Cook');
call sp_CreateTypeEmployee('Server');

-- ------------------- Read TypeEmployees -- -------------------
Delimiter $$
	Create procedure sp_ReadTypeEmployees()
    Begin 
		Select TE.codeTypeEmployee,
				TE.descript from TypeEmployees TE;
    End$$
Delimiter ;

call sp_ReadTypeEmployees();

-- ------------------- Update TypeEmployee -- -------------------
Delimiter $$
	Create procedure sp_UpdateTypeEmployee(in cTypeEmployee int, in dscpt varchar(50))
    Begin
		Start transaction;
			Update TypeEmployees TE
				set TE.descript = dscpt
					where TE.codeTypeEmployee = cTypeEmployee;
		commit;
    End$$
Delimiter ;

-- ------------------- Delete TypeEmployee -- -------------------
Delimiter $$
	Create procedure sp_DeleteTypeEmployee(in cTypeEmployee int)
	Begin
		Delete from TypeEmployees 
			where codeTypeEmployee = cTypeEmployee;
    End$$
Delimiter ;

-- ------------------- Search TypeEmployee -- -------------------
Delimiter $$
	Create procedure sp_SearchTypeEmployee(in cTypeEmployee int)
    Begin
		Select TE.codeTypeEmployee, TE.descript from TypeEmployees TE
			where TE.codeTypeEmployee = cTypeEmployee;
    End$$
Delimiter ;

-- ------------------- Storage Procedures Entity Employees -- -------------------

-- ------------------- Create Employee -- -------------------
Delimiter $$
	Create procedure sp_CreateEmployee(in numberEmployee int, in secondNameEmployee varchar(150), 
										in firstNameEmployee varchar(150), in adressEmployee varchar(150), 
                                        in contactPhone varchar(10), in cookDegree varchar(50), in codeTypeEmployee int)
    Begin
		Insert into Employees(numberEmployee, secondNameEmployee, firstNameEmployee, adressEmployee, contactPhone, 
								cookDegree, codeTypeEmployee)
					values(numberEmployee, secondNameEmployee, firstNameEmployee, adressEmployee, contactPhone, 
								cookDegree, codeTypeEmployee);
    End$$
Delimiter ;

call sp_CreateEmployee(1, 'Lee', 'David', '789 Oak St. Suite 200 Miami', '305-4604', 'Certificate in Baking and Pastry Arts', 1);
call sp_CreateEmployee(2, 'Rodriguez', 'Maria', '123 Pine St. 300 Miami', '305-2157', 'Associates Degree in Culinary Arts', 3);
call sp_CreateEmployee(3, 'Johnson', 'Emma', '456 Maple St. Suite 100 Miami', '305-3435', 'Culinary Arts', 2);

-- ------------------- Read Employees -- -------------------
Delimiter $$
	Create procedure sp_ReadEmployees()
		Begin
			Select E.codeEmployee,
					E.numberEmployee,
					E.secondNameEmployee,
                    E.firstNameEmployee,
                    E.adressEmployee,
                    E.contactPhone, 
                    E.cookDegree,
                    E.codeTypeEmployee from Employees E;
        End$$
Delimiter ;

call sp_ReadEmployees();

-- ------------------- Update Employee -- -------------------
Delimiter $$
	Create procedure sp_UpdateEmployee(in cEmployee int, in numEmployee int, in secNameEmployee varchar(150),
										in fNameEmployee varchar(150), in aEmployee varchar(150), 
                                        in contPhone varchar(10), in cookD varchar(50), in cTypeEmployee int)
		Begin
			Start transaction;
				Update Employees E
					set E.numberEmployee = numEmployee,
						E.secondNameEmployee = secNameEmployee,
						E.firstNameEmployee = fNameEmployee,
						E.adressEmployee = aEmployee, 
						E.contactPhone = contPhone,
						E.cookDegree = cookD,
						E.codeTypeEmployee = cTypeEmployee
							where E.codeEmployee = cEmployee;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete Employee -- -------------------
Delimiter $$
	Create procedure sp_DeleteEmployee(in cEmployee int)
		Begin
			Delete from Employees
				where codeEmployee = cEmployee;
        End$$
Delimiter ;

-- ------------------- Search Employee -- -------------------
Delimiter $$
	Create procedure sp_SearchEmployee(in cEmployee int)
		Begin
			Select E.codeEmployee,
					E.numberEmployee,
					E.secondNameEmployee,
                    E.firstNameEmployee,
                    E.adressEmployee,
                    E.contactPhone,
                    E.cookDegree,
                    E.codeTypeEmployee from Employees E where E.codeEmployee = cEmployee;
        End$$
Delimiter ;

-- ------------------- Storage Procedures Entity TypeDish -- -------------------

-- ------------------- Create TypeDish -- -------------------
Delimiter $$
	Create procedure sp_CreateTypeDish(in descriptionType varchar(100))
		Begin
			Insert into TypeDish(descriptionType)
				values(descriptionType);
        End$$
Delimiter ;

call sp_CreateTypeDish('American');
call sp_CreateTypeDish('Chinese');
call sp_CreateTypeDish('Mongolian');

-- ------------------- Read TypeDish -- -------------------
Delimiter $$
	Create procedure sp_ReadTypeDishes()
		Begin
			Select TD.codeTypeDish, 
					TD.descriptionType from TypeDish TD;
        End$$
Delimiter ;

call sp_ReadTypeDishes();

-- ------------------- Update TypeDish -- -------------------
Delimiter $$
	Create procedure sp_UpdateTypeDish(in cTypeDish int, in descType varchar(100))
		Begin
			Start transaction;
				Update TypeDish TD
					set TD.descriptionType = descType
						where TD.codeTypeDish = cTypeDish;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete TypeDish -- -------------------
Delimiter $$
	Create procedure sp_DeleteTypeDish(in cTypeDish int)
		Begin
			Delete from TypeDish 
				where codeTypeDish = cTypeDish;
        End$$
Delimiter ;

-- ------------------- Search TypeDish -- -------------------
Delimiter $$
	Create procedure sp_SearchTypeDish(in cTypeDish int)
		Begin
			Select TD.codeTypeDish, TD.descriptionType from TypeDish TD
				where TD.codeTypeDish = cTypeDish;
        End$$
Delimiter ;

-- ------------------- Storage Procedures Entity Products -- -------------------

-- ------------------- Create Product -- -------------------
Delimiter $$
	Create procedure sp_CreateProduct(in codeProduct int, in nameProduct varchar(150), in quantity int)
		Begin
			Insert into Products(codeProduct, nameProduct, quantity)
				values(codeProduct, nameProduct, quantity);
        End$$
Delimiter ;

call sp_CreateProduct(1, 'Onion', 20);
call sp_CreateProduct(2, 'Shrimp', 30);
call sp_CreateProduct(3, 'Pepperoni', 40);

-- ------------------- Read Products -- -------------------
Delimiter $$
	Create procedure sp_ReadProducts()
		Begin
			Select P.codeProduct,
					P.nameProduct,
					P.quantity from Products P;
        End$$
Delimiter ;

call sp_ReadProducts();

-- ------------------- Update Products -- -------------------
Delimiter $$
	Create procedure sp_UpdateProduct(in cProduct int, in nProduct varchar(150), quant int)
		Begin
			Start transaction;
				Update Products P 
					set P.nameProduct = nProduct,
						P.quantity = quant
							where P.codeProduct = cProduct;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete Product -- -------------------
Delimiter $$
	Create procedure sp_DeleteProduct(in cProduct int)
		Begin
			Delete from Products
				where codeProduct = cProduct;
        End$$
Delimiter ;

-- ------------------- Search Product -- -------------------
Delimiter $$
	Create procedure sp_SearchProduct(in cProduct int)
		Begin
			Select P.codeProduct,
					P.nameProduct,
					P.quantity from Products P where P.codeProduct = cProduct;
        End$$
Delimiter ;

-- ------------------- Storage Procedures Entity Services -- -------------------

-- ------------------- Create Service -- -------------------
Delimiter $$
	Create procedure sp_CreateService(in dateService date, in typeService varchar(150), in hourService time, 
										in placeService varchar(150), in phoneContact varchar(10), in codeCompany int)
		Begin
			Insert into Services(dateService, typeService, hourService, placeService, phoneContact, codeCompany)
				values(dateService, typeService, hourService, placeService, phoneContact, codeCompany);
        End$$
Delimiter ;

call sp_CreateService('2023-04-08', 'Buffet Service', '15:30:00', 'Spring Garden', '305-4932', 1);
call sp_CreateService('2023-04-09','Delivery Service','17:23:00','East Little Havana','305-2488', 2);
call sp_CreateService('2023-04-10','Family-Style Service','18:00:00','Brownsville','305-5932', 3);

-- ------------------- Read Services -- -------------------
Delimiter $$
	Create procedure sp_ReadServices()
		Begin
			Select S.codeService,
					S.dateService,
                    S.typeService,
                    S.hourService,
                    S.placeService,
                    S.phoneContact,
                    S.codeCompany from Services S;
        End$$
Delimiter ;

call sp_ReadServices();
-- ------------------- Update Service -- -------------------
Delimiter $$
	Create procedure sp_UpdateService(in cService int, in dService date, tService varchar(150), in hService time, 
										in pService varchar(150), in phContact varchar(10), in cCompany int)
		Begin
			Start transaction;
				Update Services S 
					set S.dateService = dService,
						S.typeService = tService, 
						S.hourService = hService, 
						S.placeService = pService, 
						S.phoneContact = phContact, 
						S.codeCompany = cCompany where S.codeService = cService;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete Service -- -------------------
Delimiter $$
	Create procedure sp_DeleteService(in cService int)
		Begin
			Delete from Services 
				where codeService = cService;
        End$$
Delimiter ;

-- ------------------- Search Service -- -------------------
Delimiter $$
	Create procedure sp_SearchService(in cService int)
		Begin
			Select S.codeService,
					S.dateService,
					S.typeService,
                    S.hourService,
                    S.placeService,
                    S.phoneContact,
                    S.codeCompany from Services S where S.codeService = cService;
        End$$
Delimiter ;

-- ------------------- Storage Procedures Entity Budgets -- -------------------

-- ------------------- Create Budget -- -------------------
Delimiter $$
	Create procedure sp_CreateBudget(in dateRequest date, in amountBudget decimal(10,2), in codeCompany int)
		Begin
			Insert into Budgets(dateRequest, amountBudget, codeCompany)
				values(dateRequest, amountBudget, codeCompany);
        End$$
Delimiter ;

call sp_CreateBudget('2023-04-08', 25000.00, 1);
call sp_CreateBudget('2023-04-09', 35000.00, 2);
call sp_CreateBudget('2023-04-10', 40000.00, 3);

-- ------------------- Read Budgets -- -------------------
Delimiter $$
	Create procedure sp_ReadBudgets()
		Begin
			Select 	B.codeBudget,
					B.dateRequest,
					B.amountBudget,
                    B.codeCompany from Budgets B;
        End$$
Delimiter ;

call sp_ReadBudgets();

-- ------------------- Update Budget -- -------------------
Delimiter $$
	Create procedure sp_UpdateBudget(in cBudget int, in dRequest date, in amBudget decimal(10,2), in cCompany int)
		Begin
			Start transaction;
				Update Budgets B
					set B.dateRequest = dRequest,
						B.amountBudget = amBudget,
                        B.codeCompany = cCompany where B.codeBudget = cBudget;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete Budget -- -------------------
Delimiter $$
	Create procedure sp_DeleteBudget(in cBudget int)
		Begin
			Delete from Budgets
				where codeBudget = cBudget;
        End$$
Delimiter ;

-- ------------------- Search Budget -- -------------------
Delimiter $$
	Create procedure sp_SearchBudget(in cBudget int)
		Begin
			Select 	B.codeBudget,
					B.dateRequest,
					B.amountBudget,
                    B.codeCompany from Budgets B where B.codeBudget = cBudget;
        End$$
Delimiter ;

-- ------------------- Storage Procedures Entity Dishes -- -------------------

-- ------------------- Create Dish -- -------------------
Delimiter $$
	Create procedure sp_CreateDish(in quantity int, in nameDish varchar(50), in descriptionDish varchar(150), 
									in priceDish decimal(10,2), in codeTypeDish int)
		Begin
			Insert into Dishes(quantity, nameDish, descriptionDish, priceDish, codeTypeDish)
				values(quantity, nameDish, descriptionDish, priceDish, codeTypeDish);
        End$$
Delimiter ;

call sp_CreateDish(150, 'Grilled Salmon with Lemon Butter Sauce', 'This dish features a fresh, grilled salmon fillet
	drizzled with a tangy lemon butter sauce.', 25.00, 1);
call sp_CreateDish(200, 'Pad Thai with Shrimp', 'Features thin rice noodles stir-fried with shrimp,
	scrambled eggs, bean sprouts, and green onions in a savory and slightly sweet tamarind sauce.',16.00, 2);
call sp_CreateDish(400, 'Khorkhog', 'Khorkhog is a traditional Mongolian dish made by cooking lamb or goat meat with hot
	rocks in a covered pot until it becomes tender and juicy.', 22.00, 3);
    
-- ------------------- Read Dishes -- -------------------
Delimiter $$
	Create procedure sp_ReadDishes()
		Begin
			Select D.codeDish, 
					D.quantity, 
                    D.nameDish,
                    D.descriptionDish, 
                    D.priceDish, 
                    D.codeTypeDish from Dishes D;
        End$$
Delimiter ;

call sp_ReadDishes();

-- ------------------- Update Dish -- -------------------
Delimiter $$
	Create procedure sp_UpdateDish(in cDish int, in quant int, in nDish varchar(50), in descriptDish varchar(150),
										in pDish decimal(10,2), in cTypeDish int)
		Begin
			Start transaction;
				Update Dishes D 
					set D.quantity = quant, 
						D.nameDish = nDish, 
						D.descriptionDish = descriptDish, 
						D.priceDish = pDish, 
						D.codeTypeDish = cTypeDish where D.codeDish = cDish;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete Dish -- -------------------
Delimiter $$
	Create procedure sp_DeleteDish(in cDish int)
		Begin
			Delete from Dishes 
				where codeDish = cDish;
        End$$
Delimiter ;


-- ------------------- Search Dish -- -------------------
Delimiter $$
	Create procedure sp_SearchDish(in cDish int)
		Begin
			Select D.codeDish,
					D.quantity,
					D.nameDish,
                    D.descriptionDish,
                    D.priceDish,
                    D.codeTypeDish from Dishes D where D.codeDish = cDish;
        End$$
Delimiter ;

-- ------------------- Storage Procedures Entity Products_has_Dishes -- -------------------

-- ------------------- Create Product_has_Dish -- -------------------
Delimiter $$
	Create procedure sp_CreateProduct_has_Dish(in Products_codeProduct int, in codeDish int, in codeProduct int)
		Begin
			Insert into Products_has_Dishes(Products_codeProduct, codeDish, codeProduct)
				values(Products_codeProduct, codeDish, codeProduct);
        End$$
Delimiter ;

call sp_CreateProduct_has_Dish(1,1,1);
call sp_CreateProduct_has_Dish(2,2,2);
call sp_CreateProduct_has_Dish(3,3,3);

-- ------------------- Read Products_has_Dishes -- -------------------
Delimiter $$
	Create procedure sp_ReadProduct_has_Dishes()
		Begin
			Select PhD.Products_codeProduct,
					PhD.codeDish, 
                    PhD.codeProduct from Products_has_Dishes PhD;
        End$$
Delimiter ;	

call sp_ReadProduct_has_Dishes();

-- ------------------- Update Product_has_Dish -- -------------------
Delimiter $$
	Create procedure sp_UpdateProduct_has_Dish(in P_cProduct int, in cDish int, in cProduct int)
		Begin
			Start transaction;
				Update Products_has_Dishes PhD
					set PhD.Products_codeProduct = p_cProduct,
						PhD.codeDish = cDish,
						PhD.codeProduct = cProduct where PhD.Products_codeProduct = p_cProduct;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete Product_has_Dish -- -------------------
Delimiter $$
	Create procedure sp_DeleteProduct_has_Dish(in P_cProduct int)
		Begin
			Delete from Products_has_Dishes
				where Products_codeProduct = p_cProduct;
        End$$
Delimiter ;

-- ------------------- Search Product_has_Dish -- -------------------
Delimiter $$
	Create procedure sp_SearchProduct_has_Dish(in P_cProduct int)
		Begin
			Select PhD.codeDish,
					PhD.codeProduct from Products_has_Dishes PhD where PhD.Products_codeProduct = P_cProduct;
        End$$
Delimiter ;

-- ------------------- Storage Procedures Entity Services_has_Dishes -- -------------------

-- ------------------- Create Service_has_Dish -- -------------------
Delimiter $$
	Create procedure sp_CreateService_has_Dish(in Services_codeService int, in codeDish int, in codeService int)
		Begin
			Insert into Services_has_Dishes(Services_codeService, codeDish, codeService)
				values(Services_codeService, codeDish, codeService);
        End$$
Delimiter ;

call sp_CreateService_has_Dish(1, 1, 1);
call sp_CreateService_has_Dish(2, 2, 2);
call sp_CreateService_has_Dish(3, 3, 3);

-- ------------------- Read Services_has_Dishes -- -------------------
Delimiter $$	
	Create procedure sp_ReadServices_has_Dishes()
		Begin
			Select ShD.Services_codeService, 
					ShD.codeDish, 
                    ShD.codeService from Services_has_Dishes ShD;
        End$$
Delimiter ;

call sp_ReadServices_has_Dishes();

-- ------------------- Update Service_has_Dish -- -------------------
Delimiter $$
	Create procedure sp_UpdateService_has_Dish(in S_cService int, in cDish int, in cService int)
		Begin
			Start transaction;
				Update Services_has_Dishes ShD
					set ShD.Services_codeService = S_cService,
						ShD.codeDish = cDish,
						ShD.codeService = cService where ShD.Services_codeService = S_cService;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete Service_has_Dish -- -------------------
Delimiter $$
	Create procedure sp_DeleteService_has_Dish(in S_cService int)
		Begin
			Delete from Services_has_Dishes 
				where Services_codeService = S_cService;
        End$$
Delimiter ;

-- ------------------- Search Service_has_Dish -- -------------------
Delimiter $$
	Create procedure sp_SearchService_has_Dish(in S_cService int)
		Begin
			Select ShD.codeDish, 
                    ShD.codeService from Services_has_Dish ShD where ShD.Services_codeService = S_cService;
        End$$
Delimiter ;

-- ------------------- Storage Procedures Services_has_Employees -- -------------------

-- ------------------- Create Service_has_Employee -- -------------------
Delimiter $$
	Create procedure sp_CreateService_has_Employee(in Services_codeService int, in codeService int, in codeEmployee int, 
													in dateEvent date, in hourEvent time, in placeEvent varchar(150))
		Begin
			Insert into Services_has_Employees(Services_codeService, codeService, codeEmployee, dateEvent, hourEvent, placeEvent)
				values(Services_codeService, codeService, codeEmployee, dateEvent, hourEvent, placeEvent);
        End$$
Delimiter ;

call sp_CreateService_has_Employee(1, 1, 1, '2023-04-08', '15:30:00', 'Spring Garden');
call sp_CreateService_has_Employee(2, 2, 2, '2023-04-09','17:23:00','East Little Havana');
call sp_CreateService_has_Employee(3, 3, 3, '2023-04-10', '18:00:00', 'Brownsville');

-- ------------------- Read Services_has_Employees -- -------------------
Delimiter $$
	Create procedure sp_ReadServices_has_Employees()
		Begin
			Select ShE.Services_codeService, 
					ShE.codeService, 
                    ShE.codeEmployee, 
					ShE.dateEvent, 
                    ShE.hourEvent, 
                    ShE.placeEvent from Services_has_Employees ShE;
        End$$
Delimiter ;

call sp_ReadServices_has_Employees();

-- ------------------- Update Service_has_Employee -- -------------------
Delimiter $$
	Create procedure sp_UpdateService_has_Employee(in S_cService int, in cService int, in cEmployee int, in dEvent date, 
													in hEvent time, in pEvent varchar(150))
		Begin
			Start transaction;
				Update Services_has_Employees ShE
					set ShE.codeService = cService, 
						ShE.codeEmployee = cEmployee, 
						ShE.dateEvent = dEvent, 
						ShE.hourEvent = hEvent, 
						ShE.placeEvent = pEvent where ShE.Services_codeService = S_cService;
			commit;
        End$$
Delimiter ;

-- ------------------- Delete Service_has_Employee -- -------------------
Delimiter $$
	Create procedure sp_DeleteService_has_Employee(in S_cService int)
		Begin
			Delete from Services_has_Employees 
				where Services_codeService = S_cService;
        End$$
Delimiter ;

-- ------------------- Search Service_has_Employee -- -------------------
Delimiter $$
	Create procedure sp_SearchService_has_Employee(in S_cService int)
		Begin
			Select ShE.codeService, 
					ShE.codeEmployee, 
                    ShE.dateEvent, 
                    ShE.hourEvent, 
                    ShE.placeEvent from Services_has_Employees ShE where ShE.Services_codeService = S_cService;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_Report()
		Begin
			Select C.nameCompany, B.amountBudget ,S.typeService, E.firstNameEmployee, E.secondNameEmployee, TE.descript, 
				   D.nameDish, TD.descriptionType, D.quantity, D.priceDish, P.nameProduct, P.quantity 
			From Companys C
				INNER JOIN Budgets B on C.codeCompany = B.codeCompany
				INNER JOIN Services S on S.codeService = B.codeCompany
				INNER JOIN Services_has_Employees ShE on ShE.codeService = S.codeService
				INNER JOIN Employees E on E.codeEmployee = ShE.codeEmployee
				INNER JOIN TypeEmployees TE on TE.codeTypeEmployee = E.codeTypeEmployee
				INNER JOIN Services_has_Dishes ShD on ShD.codeService = S.codeService
				INNER JOIN Dishes D on D.codeDish = ShD.codeDish
				INNER JOIN TypeDish TD on TD.codeTypeDish = D.codeTypeDish
				INNER JOIN Products_has_Dishes PhD ON PhD.codeDish = D.codeDish
				INNER JOIN Products P on P.codeProduct = PhD.codeProduct; 
        End$$
Delimiter ;

call sp_Report();