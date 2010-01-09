import pg,sys

con = pg.connect(dbname='music',host='hancock',user='music')

#Move all boxed records into the to-shelve box
sql = "UPDATE records set boxed = 0 where boxed = -1"
print sql
res = con.query(sql)


#Pick out 25 records at random
sql = "update records set boxed = -1 where recordnumber in (select recordnumber from records,score_table,formats where boxed != -1 AND recordnumber = record_id AND (owner = 1 OR simon_score >= 8) AND format = formatnumber AND baseformat = '12'  AND salepricepence < 0 ORDER BY RANDOM() LIMIT 20)";
print sql
res = con.query(sql)

#10 inches
sql = "update records set boxed = -1 where recordnumber in (select recordnumber from records,score_table,formats where boxed != -1 AND recordnumber = record_id AND (owner = 1 OR simon_score >= 8) AND format = formatnumber AND baseformat = '10'  AND salepricepence < 0 ORDER BY RANDOM() LIMIT 5)";
print sql
res = con.query(sql)

# CDs
sql = "update records set boxed = -1 where recordnumber in (select recordnumber from records,score_table,formats where boxed != -1 AND recordnumber = record_id AND (owner = 1 OR simon_score >= 8) AND format = formatnumber AND baseformat = 'CD' AND salepricepence < 0 ORDER BY RANDOM() LIMIT 25)";
print sql
res = con.query(sql)

# 7"s
sql = "update records set boxed = -1 where recordnumber in (select recordnumber from records,score_table,formats where boxed != -1 AND recordnumber = record_id AND (owner = 1 OR simon_score >= 8) AND format = formatnumber AND baseformat = '7' AND salepricepence < 0 ORDER BY RANDOM() LIMIT 20)";
print sql
res = con.query(sql)
