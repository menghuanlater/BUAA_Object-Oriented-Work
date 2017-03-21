write at 2017 3.22 01：30
<img src=https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=Never%20give%20up&step_word=&hs=0&pn=3&spn=0&di=101126082860&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3892740038%2C2709498431&os=170385204%2C2164153543&simid=3398175221%2C28109640&adpicid=0&lpn=0&ln=1981&fr=&fmq=1490118735719_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fupload.news.cecb2b.com%2F2013%2F0606%2F1370484410836.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fgjof_z%26e3Bvjvkdk_z%26e3Bv54AzdH3Ftgu5AzdH3Fda8namamAzdH3Fmcdblm_z%26e3Bfip4s&gsm=0&rpstart=0&rpnum=0 />
1.当主请求每一次循环找寻所有可以直接捎带完成的指令并且选择当前最优先捎带请求完成时，跳出循环体，一开始并没有注意到应该要checkSame一下，因为主请求的完成时间发生了变化(although 一开始考虑到了要这么做，但是写的过程中并没有注意到)
2.对于找寻到捎带直接完成类的请求的时候，并没有考虑好何时删除它们(造成索引变化问题）
3.对于查找可获取主请求控制权且本身是主请求的捎带请求的时候，没有判断请求时间是否超出主请求相关要求时间范围
4. some advice for later all projects：
<li>首先考虑好所有可能出现的情况，把可能出现的大致罗列一遍，写出考虑的疑难点和预估的实现方法
</li>
<li>其次，架构好设计的时候，理清每一步的循环逻辑、判断逻辑。先推演，根据第一步出现的情况看自己设计的逻辑是否正确,再实现代码
</li>
<li>测试的时候，针对架构时考虑到的所有的情况，设计测试样例，验证基本功能是否正确
</li>
<li>最后，实行混合测试，正常+极端
</li>
<li>Last But Not Least,不要感觉写下来很费事，其实往往会达到事半工倍的效果，低级bug数会减少很多
</li>
