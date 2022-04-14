# Safe and Simple Android BLE demo using Kotlin Coroutines

This project demostrates how to use the BLE APIs in Android in a simple and safe way. 
The `bluetooth` module provides an API that will queue each BLE operation to ensure you only ever have one operation in flight at any time. 
This will remove one of the most common errors with BLE, where executing a new write operation before the first one is complete might result in 
unexpected behaviours. 

Feel free to copy and use this as you wish. At some time I might make a real library of this. PRs are welcome! ;)
