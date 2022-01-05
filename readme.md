## prepare redis-cluster
1. download and install `redis-5.0.14`  
2. create cluster with help of `utils/create-cluster`
```bash
./create-cluster start
./create-cluster create
```

## Steps to reproduce
0. Run `com.example.demoDemoApplication`
1. Open browser, access `http://localhost:8080/hello`, a series of characters like "worldworld..." should be presented
2. Enter cluster cli `redis-cli -p 30001 -c`, check cluster status: `cluster info` 
3. Examine cluster nodes status `cluster nodes`
4. Kill one of the cluster node process
5. Check cluster status: `cluster info`, cluster status should be OK after quick fail-over 
6. access `http://localhost:8080/hello` again (or just refresh the page), the `JedisConnectionException` shows up
6. refresh the `hello` page ,  everything is okay again.
