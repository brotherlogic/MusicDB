mvn clean install webstart:jnlp
rsync -avz --rsh=ssh --progress ./target/jnlp/ hancock:/home/sat/workspace/musicdatabase/app