package ua.krem.agent.dao;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ua.krem.agent.model.DocHead;
import ua.krem.agent.model.DocHeadFilter;
import ua.krem.agent.model.DocList;
import ua.krem.agent.model.Document;
import ua.krem.agent.model.Item;

@Repository
public class DocumentDAO {

	private JdbcTemplate jdbcTemplate;
	private ProductDAO productDAO;
	
	@Inject
	public DocumentDAO(JdbcTemplate jdbcTemplate, ProductDAO productDAO){
		this.jdbcTemplate = jdbcTemplate;
		this.productDAO = productDAO;
	}
	
	public List<DocHead> selectDoc(DocHeadFilter filter){
		List<DocHead> docList = new ArrayList<DocHead>();
		if(filter == null){
			String sql = "SELECT doc_id id, date, add1, add2, type FROM doc ORDER BY id limit 50";
			try{
				List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
				for(Map<String, Object> map : mapList){
					docList.add(extractDocHead(map));
				}
			}catch(EmptyResultDataAccessException e){
				e.printStackTrace();
			}
		} else {
			System.out.println("filter.userId = " + filter.getUserId());
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT doc_id id, date, add1, add2, type FROM doc ");
			if(filter.getUserId() != null){
				sql.append(" WHERE user_id = ").append(filter.getUserId());
			}
			
			if(filter.getAdd1() != null && !filter.getAdd1().isEmpty()){
				if(sql.indexOf("WHERE") != -1){
					sql.append(" AND ");
				} else {
					sql.append(" WHERE ");
				}
				sql.append(" add1 LIKE '%").append(filter.getAdd1()).append("%' ");
			}

			if(filter.getAdd2() != null && !filter.getAdd2().isEmpty()){
				if(sql.indexOf("WHERE") != -1){
					sql.append(" AND ");
				} else {
					sql.append(" WHERE ");
				}
				sql.append(" add2 LIKE '%").append(filter.getAdd2()).append("%' ");
			}

			if(filter.getDocType() != null){
				if(sql.indexOf("WHERE") != -1){
					sql.append(" AND ");
				} else {
					sql.append(" WHERE ");
				}
				sql.append(" type = ").append(filter.getDocType());
			}

			if(filter.getFrom() != null && filter.getTo() != null && !filter.getFrom().isEmpty() && !filter.getTo().isEmpty()){
				if(sql.indexOf("WHERE") != -1){
					sql.append(" AND ");
				} else {
					sql.append(" WHERE ");
				}
				sql.append(" date BETWEEN '").append(filter.getFrom()).append("' AND '").append(filter.getTo()).append("' ");
			}
			else 
			{
				if (filter.getFrom()!=null && !filter.getFrom().isEmpty())
				{
					if(sql.indexOf("WHERE") != -1){
						sql.append(" AND ");
					} else {
						sql.append(" WHERE ");
					}
					sql.append(" date > '").append(filter.getFrom()).append("' ");
				}
				else
				{
					if (filter.getTo()!=null && !filter.getTo().isEmpty())
					{
						if(sql.indexOf("WHERE") != -1){
							sql.append(" AND ");
						} else {
							sql.append(" WHERE ");
						}
						sql.append(" date < '").append(filter.getTo()).append("' ");
					}
				}
				

			}
			
			//if user comes without session
			if(sql.indexOf("WHERE") != -1 && sql.indexOf("=") == -1){
				sql = new StringBuilder(sql.substring(0, sql.indexOf("WHERE")));
			}
			
			if(sql.indexOf("date >") == -1 && sql.indexOf("date <") == -1 && sql.indexOf("BETWEEN") == -1){
				if(sql.indexOf("WHERE") != -1){
					sql.append(" AND YEAR(date) = YEAR(NOW()) AND MONTH(date) = MONTH(NOW()) AND DAY(date) = DAY(NOW())");
				} else {
					sql.append(" WHERE YEAR(date) = YEAR(NOW()) AND MONTH(date) = MONTH(NOW()) AND DAY(date) = DAY(NOW())");
				}
			}
			
			sql.append(" ORDER BY id limit 50");
			System.out.println(sql.toString());
			try{
				List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql.toString());
				for(Map<String, Object> map : mapList){
					docList.add(extractDocHead(map));
				}
			}catch(EmptyResultDataAccessException e){
				e.printStackTrace();
			}
			
			
		}
		
		return docList;
	}
	
	private DocHead extractDocHead(Map<String, Object> map) {
		DocHead docHead = new DocHead();
		if(map != null && !map.isEmpty()){
			docHead.setId((Integer)map.get("id"));
			docHead.setDate((Date)map.get("date"));
			docHead.setAdd1((String)map.get("add1"));
			docHead.setAdd2((String)map.get("add2"));
			if((Integer)map.get("type") == 1){
				docHead.setType("Возврат");
			} else {
				docHead.setType("Реализация");
			}
		}
		return docHead;
	}
	
	public Document getDocumentById(Integer docId){
		String sql = "SELECT shop_id, type FROM doc WHERE doc_id = ?";
		Document doc = new Document();
		doc.setId(docId);
		try{
			Map<String, Object> map = jdbcTemplate.queryForMap(sql, docId);
			if(map != null && !map.isEmpty()){
				doc.setShopId((Integer)map.get("shop_id"));
				doc.setDocType((Integer)map.get("type"));
				
				sql = "SELECT prod_id, amount FROM doc_element WHERE doc_id = ?";
				List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql.toString(), docId);
				List<Item> itemList = new ArrayList<Item>();
				for(Map<String, Object> m : mapList){
					if(m.get("amount") != null && !((Integer)m.get("amount")).toString().isEmpty()){
						Item item = new Item();
						item.id = (Integer)m.get("prod_id");
						item.amount = (Integer)m.get("amount");
						itemList.add(item);
					}
				}
				doc.setItemList(itemList);
				doc.setProductList( productDAO.getProducts(null, itemList) );
				
			}
		}catch(EmptyResultDataAccessException e){
			e.printStackTrace();
		}
		
		return doc;
	}

	public String exportDocuments(DocList docs, String uid, String path) {
		String result = "Документы успешно экспортированы!";
		String sql = "select CONCAT_WS(\"; \", X.doc_id, X.date, X.type, X.shop_id, Y.code, Y.name) AS Col from doc X, shop Y WHERE X.doc_id = ? AND X.shop_id = Y.shop_id";
		String sqlProd = "select CONCAT_WS(\"; \", X.doc_id, Y.code, Y.name,  X.amount) AS Col from doc_element X, product Y where X.doc_id=? AND Y.prod_id=X.prod_id";
		int[] id = docs.getDocId();
		int[] flag = docs.getChecked();	
		if(flag.length==0) return "Ошибка экспорта: ничего не было выбрано!"; 
		try{
			FileWriter rfile = new FileWriter(path + "export_doc_heads_"+ uid +".csv");
			FileWriter prodfile = new FileWriter(path + "export_doc_elements_"+ uid +".csv");
			rfile.write("ID; Date; Type; Shop_ID; Shop_Code; Shop_Name \n");
			prodfile.write("ID; ProdCode; ProdName; Amount \n");
			for(int i=0; i<flag.length;i++)
			{
				List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql.toString(), id[i]);
				List<Map<String, Object>> mapListProd = jdbcTemplate.queryForList(sqlProd.toString(), id[i]);
				rfile.write(mapList.get(0).get("Col").toString());
				rfile.write("\n");
				for(Map<String, Object> el : mapListProd)
				{
					prodfile.write(el.get("Col").toString());
					prodfile.write("\n");
				}
			}
			System.out.println(path);
			rfile.close(); //Список документов
			prodfile.close(); //Список продуктов
		}
		catch(EmptyResultDataAccessException e){
			e.printStackTrace();
			return "Ошибка экпорта!";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Ошибка экпорта!";
		}
		return result;
	}
	
}
