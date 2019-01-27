# P2P Network

Node Arguments:

- IP Address
- Port
- Neighbour Node IP Address (Optional)
- Neighbour Node Port (Optional)

Example 1 : java -cp . Node localhost 1025 Example 2 : java -cp . Node localhost 1025 localhost 1026

PutClient Arguments:

- IP Address Of Node
- Port Of Node
- Key
- Message

Example 1 : java -cp . PutClient localhost 1025 1 A

GetClient Arguments:

- IP Address
- IP Address Of Node
- Port Of Node
- Key

Example 1 : java -cp . GetClient localhost localhost 1025 1

