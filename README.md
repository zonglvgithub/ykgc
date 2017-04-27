## 时间选择空间

1.

  if(i%3==0 ){
                    TeamInfo teamInfo = null;
                    if(i<4){
                        teamInfo = new TeamInfo("","",0,4);//无效灰色过期不能选状态****
                        i=5;
                        list.add(teamInfo);
                        continue;
                    }
                    if(i/3==2){
                        teamInfo = new TeamInfo("测试数据",String.valueOf(i),i,i+2);
                    }else {
                        teamInfo = new TeamInfo("测试数据",String.valueOf(i),i,i+1);
                    }
                    list.add(teamInfo);
                }

2.

手动设置scrollView的宽高
 viewHolder.timeChooseView.setWidthHeight(ScreenUtil.dip2px(activity, 80));// 重新绘制宽高，不然自定义控件放在ScrollView里面没有高度不显示

3.

4.

5.

6.





