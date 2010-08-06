mvn clean install webstart:jnlp
rsync -avz --rsh=ssh --progress --delete-after ./target/jnlp/ sat@edip:/home/sat/websites/mdb/