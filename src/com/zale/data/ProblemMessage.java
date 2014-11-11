package com.zale.data;

import android.util.Log;


public class ProblemMessage {
	
	private int id;
	private String node_name;
	private String sid;
	private String stime;
	private String etime;

	
	@Override
	public String toString() {
		return "Problem [id=" +String.valueOf(id)+
				"node_name=" +node_name+
				"sid=" +sid+
				"stime=" +stime+ "]";
	}
	
	/**
	 * @param id 消息ID
	 * @param node_name 模块名称
	 * @param sid 模块ID
	 * @param stime 故障时间

	 */
	public ProblemMessage(int id ,String node_name, String sid, String stime,String etime){
		this.id = id;
		this.node_name = node_name;
		this.sid = sid;
		this.stime = stime;
		this.etime = etime;
	}
	
//	//计算出哈希码
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ID;
//		result = prime * result + ((PID == null) ? 0 : PID.hashCode());
//		result = prime * result
//				+ ((ProblemDetail == null) ? 0 : ProblemDetail.hashCode());
//		result = prime * result + ProblemType;
//		result = prime * result + ((SID == null) ? 0 : SID.hashCode());
//		result = prime * result + ((STime == null) ? 0 : STime.hashCode());
//		result = prime * result
//				+ ((location_detail == null) ? 0 : location_detail.hashCode());
//		result = prime * result
//				+ ((location_main == null) ? 0 : location_main.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ProblemType other = (ProblemType) obj;
//		if (ID != other.ID)
//			return false;
//		if (PID == null) {
//			if (other.PID != null)
//				return false;
//		} else if (!PID.equals(other.PID))
//			return false;
//		if (ProblemDetail == null) {
//			if (other.ProblemDetail != null)
//				return false;
//		} else if (!ProblemDetail.equals(other.ProblemDetail))
//			return false;
//		if (ProblemType != other.ProblemType)
//			return false;
//		if (SID == null) {
//			if (other.SID != null)
//				return false;
//		} else if (!SID.equals(other.SID))
//			return false;
//		if (STime == null) {
//			if (other.STime != null)
//				return false;
//		} else if (!STime.equals(other.STime))
//			return false;
//		if (location_detail == null) {
//			if (other.location_detail != null)
//				return false;
//		} else if (!location_detail.equals(other.location_detail))
//			return false;
//		if (location_main == null) {
//			if (other.location_main != null)
//				return false;
//		} else if (!location_main.equals(other.location_main))
//			return false;
//		return true;
//	}

	/**
	 * @return 返回对这一个错误的具体描述
	 */
	public String toShowString(){
		
		return new String(
				"\n模块ID:" +this.node_name+
				"\n模块模块:" + this.sid +  
				"\n故障时间:" + this.stime + 
				"\n处理时间:" + this.etime + 
				"\n");
	}
	
	/**
	 * @return 返回故障信息名称
	 */
	public String get_node_name()
	{
		return new String(this.node_name);
	}
	

	
	/**
	 * @return 返回故障发生的时间
	 */
	public String getsTime(){
		return new String(this.stime);
	}
	
	/**
	 * @return 故障ID
	 */
	public int getID(){
		return Integer.valueOf(this.id);
	}
	
	/**
	 * @return 故障种类
	 */
//	public int getProblemType(){
//		return Integer.valueOf(this.s_id);
//	}
	
	/**
	 * @param _problem 进行比较的对象
	 * @return 是否相同
	 */
//	public boolean equals(ProblemType _problem){
//		if (ID == _problem.getID()) {
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * @return SID是模块ID
	 */
	public String getSid() {
		return new String(this.sid);
	}

	/**
	 * @return PID为模块的子模块
	 */
//	public String getPid() {
//		return new String(PID);
//	}
	public String getEtime()
	{
		return this.etime;
	}
}