# 🏠 Hostel Management System

**Complete Java-based hostel management with Access database**

## 📁 Database Setup

### Database File
- Uses **Microsoft Access (.accdb file)**
- File location: `C:/Users/YourName/Documents/Hostel.accdb`
- No server needed - single file database

### Main Database Tables:
1. **Users** - Login accounts (Admin, Student, Office, Warden)
2. **Students** - Student details (name, registration, department)
3. **Fee_Structure** - Fee types & amounts (Hostel Fee, Mess Fee)
4. **Fee_Challan** - Student payment records
5. **Hostel** - Hostel building information
6. **Room** - Room details
7. **Mess_Registration** - Mess subscription data
8. **Complaints** - Student complaints
9. **Cleaning_Requests** - Cleaning service requests
10. **Room_Allocation** - Room assignment records

### Database Features:
- ✅ **Auto-creates tables** on first run
- ✅ **Single file** - easy to backup/transfer
- ✅ **Works offline** - no internet needed
- ✅ **Windows compatible** - runs on any Windows PC
- ✅ **Data security** - file can be password protected

## 🚀 Quick Setup

### 1. Requirements
- Java JDK 8 or higher
- Eclipse IDE
- Microsoft Access (optional, for viewing database)

### 2. Installation Steps:
```bash
1. Download project files
2. Import into Eclipse
3. Add 5 JAR files (ucanaccess, hsqldb, jackcess, commons-lang3, commons-logging)
4. Create config.properties with database path
5. Run LoginForm.java
```

### 3. Database Config:
Create `config.properties` file:
```properties
# Path to your Access database
db.path=C:/Users/YourName/Documents/Hostel.accdb
```

## 🔐 Login Info

| Role | Username | Password | Access |
|------|----------|----------|--------|
| Admin | hurr | 12345678 | Full system |
| Student | faizan | 1234 | Student services |
| Office | office | office123 | Room allocation |
| Warden | warden | warden123 | Complaints |

## 💰 Fee System Flow
```
Admin sets fees → Student views → Generates challan → 
Marks as paid → Admin verifies → Status: VERIFIED
```

## 📊 Key Features
- **Student Portal**: Applications, fees, complaints
- **Admin Panel**: User management, fee setup, verification
- **Office Module**: Room allocation, verification
- **Warden Module**: Complaint monitoring

## 🛠️ Tech Stack
- **Frontend**: Java Swing
- **Backend**: Java
- **Database**: Microsoft Access
- **Driver**: UCanAccess JDBC

## ❓ Common Issues
1. **Database not found** → Check path in config.properties
2. **Driver error** → Ensure all 5 JAR files added
3. **Login failed** → Use default credentials

## 📞 Support
Contact your developer for database setup help.

*Simple, efficient, no server required*
