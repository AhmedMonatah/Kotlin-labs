//enum class ItemType {
//    BOOK,
//    MAGAZINE,
//    JOURNAL
//}
//
//data class LibraryItem(
//    val title: String,
//    val isbn: String,
//    val publication: String,
//    val pages: Int,
//    val type: ItemType
//)
//
//open class Person(val name: String, val id: String)
//class Librarian(name: String, id: String, val password: String) : Person(name, id)
//class User(name: String, id: String, val job: String) : Person(name, id)
//
//class LibrarySystem {
//    private val availableItems = mutableListOf<LibraryItem>()
//    private val borrowedItems = mutableMapOf<LibraryItem, User>()
//
//    fun addItem(item: LibraryItem) {
//        availableItems.add(item)
//        println("Item added: ${item.title}")
//    }
//
//    fun viewItems(type: ItemType) {
//        var found = false
//        for (item in availableItems) {
//            if (item.type == type) {
//                println("- ${item.title}")
//                found = true
//            }
//        }
//        if (!found) println("No items found.")
//    }
//
//    fun searchItem(title: String): LibraryItem? {
//        for (item in availableItems)
//            if (item.title.equals(title, true))
//                return item
//
//        return null
//    }
//
//    fun lendItem(item: LibraryItem, user: User) {
//        availableItems.remove(item)
//        borrowedItems[item] = user
//        println("${user.name} borrowed ${item.title}")
//    }
//
//    fun receiveItemFromBorrower(title: String) {
//
//        for (item in borrowedItems.keys) {
//
//            if (item.title.equals(title, true)) {
//                borrowedItems.remove(item)
//                availableItems.add(item)
//                println("$title returned successfully.")
//                return
//            }
//        }
//        println("This item is not borrowed.")
//    }
//}
//
//fun main() {
//    val librarian = Librarian("admin", "31", "1234")
//    val library = LibrarySystem()
//
//    while (true) {
//        println("1. admin  2. user  0. exit")
//        print("choose: ")
//        when (readLine()!!) {
//            "1" -> {
//                print("password: ")
//                if (readLine() != librarian.password) {
//                    println("wrong password!"); continue
//                }
//                while (true) {
//                    println("1. add Item  2. logout")
//                    print("choose: ")
//                    when (readLine()!!) {
//                        "1" -> {
//                            print("title: ");
//                            val title = readLine()!!
//                            print("ISBN: ");
//                            val isbn = readLine()!!
//                            print("publication: ");
//                            val pub = readLine()!!
//                            print("pages: "); val pages = readLine()!!.toInt()
//                            println("Type: 1.book 2.magazine 3.journal")
//                            val type = when (readLine()!!) {
//                                "1" -> ItemType.BOOK
//                                "2" -> ItemType.MAGAZINE
//                                else -> ItemType.JOURNAL
//                            }
//                            library.addItem(LibraryItem(title,isbn,pub,pages,type))
//                        }
//                        "2" -> { println("admin logged out."); break }
//                    }
//                }
//            }
//            "2" -> {
//                print("name: ");
//                val name = readLine()!!
//                print("ID: ");
//                val id = readLine()!!
//                print("job: ");
//                val job = readLine()!!
//                val user = User(name,id,job)
//                while (true) {
//                    println("1.view items 2.borrow item 3.return item 4.logout")
//                    print("choose: ")
//                    when (readLine()!!) {
//                        "1" -> {
//                            println("1.book 2.magazine 3.journal");
//                            print("choose: ")
//                            val type = when (readLine()!!) {
//                                "1" -> ItemType.BOOK
//                                "2" -> ItemType.MAGAZINE
//                                else -> ItemType.JOURNAL
//                            }
//                            library.viewItems(type)
//                        }
//                        "2" -> {
//                            print("enter title to borrow: ")
//                            val item = library.searchItem(readLine()!!)
//                            if (item != null) library.lendItem(item,user) else println("Item not found")
//                        }
//                        "3" -> {
//                            print("enter title to return: ")
//                            library.receiveItemFromBorrower(readLine()!!)
//                        }
//                        "4" -> { println("eser logged out."); break }
//                    }
//                }
//            }
//            "0" -> {
//                break }
//        }
//    }
//}

// تعريف sealed class لكل نوع عنصر في المكتبة
sealed class ItemType(val description: String) {
    object Book : ItemType("Book")
    object Magazine : ItemType("Magazine")
    object Journal : ItemType("Journal")
}

// بيانات العنصر
data class LibraryItem(
    val title: String,
    val isbn: String,
    val publication: String,
    val pages: Int,
    val type: ItemType
)

// الأشخاص
open class Person(val name: String, val id: String)
class Librarian(name: String, id: String, val password: String) : Person(name, id)
class User(name: String, id: String, val job: String) : Person(name, id)

// نظام المكتبة
class LibrarySystem {
    private val availableItems = mutableListOf<LibraryItem>()
    private val borrowedItems = mutableMapOf<LibraryItem, User>()

    fun addItem(item: LibraryItem) {
        availableItems.add(item)
        println("Item added: ${item.title}")
    }

    fun viewItems(type: ItemType) {
        var found = false
        for (item in availableItems) {
            if (item.type::class == type::class) {
                println("- ${item.title}")
                found = true
            }
        }
        if (!found) println("No items found.")
    }

    fun searchItem(title: String): LibraryItem? {
        return availableItems.find { it.title.equals(title, true) }
    }

    fun lendItem(item: LibraryItem, user: User) {
        availableItems.remove(item)
        borrowedItems[item] = user
        println("${user.name} borrowed ${item.title}")
    }

    fun receiveItemFromBorrower(title: String) {
        val item = borrowedItems.keys.find { it.title.equals(title, true) }
        if (item != null) {
            borrowedItems.remove(item)
            availableItems.add(item)
            println("$title returned successfully.")
        } else {
            println("This item is not borrowed.")
        }
    }
}

fun test() {
    val librarian = Librarian("admin", "31", "1234")
    val library = LibrarySystem()

    while (true) {
        println("1. admin  2. user  0. exit")
        print("choose: ")
        when (readLine()!!) {
            "1" -> {
                print("password: ")
                if (readLine() != librarian.password) {
                    println("wrong password!"); continue
                }
                while (true) {
                    println("1. add Item  2. logout")
                    print("choose: ")
                    when (readLine()!!) {
                        "1" -> {
                            print("title: ")
                            val title = readLine()!!
                            print("ISBN: ")
                            val isbn = readLine()!!
                            print("publication: ")
                            val pub = readLine()!!
                            print("pages: ")
                            val pages = readLine()!!.toInt()
                            println("Type: 1.book 2.magazine 3.journal")
                            val type = when (readLine()!!) {
                                "1" -> ItemType.Book
                                "2" -> ItemType.Magazine
                                else -> ItemType.Journal
                            }
                            library.addItem(LibraryItem(title, isbn, pub, pages, type))
                        }
                        "2" -> { println("admin logged out."); break }
                    }
                }
            }
            "2" -> {
                print("name: ")
                val name = readLine()!!
                print("ID: ")
                val id = readLine()!!
                print("job: ")
                val job = readLine()!!
                val user = User(name, id, job)
                while (true) {
                    println("1.view items 2.borrow item 3.return item 4.logout")
                    print("choose: ")
                    when (readLine()!!) {
                        "1" -> {
                            println("1.book 2.magazine 3.journal")
                            print("choose: ")
                            val type = when (readLine()!!) {
                                "1" -> ItemType.Book
                                "2" -> ItemType.Magazine
                                else -> ItemType.Journal
                            }
                            library.viewItems(type)
                        }
                        "2" -> {
                            print("enter title to borrow: ")
                            val item = library.searchItem(readLine()!!)
                            if (item != null) library.lendItem(item, user) else println("Item not found")
                        }
                        "3" -> {
                            print("enter title to return: ")
                            library.receiveItemFromBorrower(readLine()!!)
                        }
                        "4" -> { println("user logged out."); break }
                    }
                }
            }
            "0" -> break
        }
    }
}

fun main(){
setCalc("sum")
    println(calc?.invoke(8,3))
    setCalc("sub")
    println(calc?.invoke(8,3))
    donothing(::foo)

}
fun foo(){

}
fun donothing(pr:(()->Unit)){

}
fun setCalc(str:String){
    when(str){
        "sum"->calc={a,b->a+b}
        "sub"->calc={a,b->a-b}
    }
}
var calc:((Int,Int)->Int)?=null