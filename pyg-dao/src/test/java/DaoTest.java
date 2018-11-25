import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.pyg.mapper.TbBrandMapper;
import com.pyg.pojo.TbBrand;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.util.List;

public class DaoTest {

    @Test
    public void init() throws Exception{

        IKAnalyzer analyzer = new IKAnalyzer();

        ClassPathXmlApplicationContext cpx  = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");

        SqlSessionFactory sqlSessionFactoryBean = cpx.getBean( SqlSessionFactory.class);

        SqlSession sqlSession = sqlSessionFactoryBean.openSession();

        TbBrandMapper mapper = sqlSession.getMapper(TbBrandMapper.class);

        List<TbBrand> allBrand = mapper.findAllBrand();

        for (TbBrand tbBrand : allBrand) {
            System.out.println(tbBrand);
        }

    }

    @Test
    public void add(){

        ClassPathXmlApplicationContext cpx  = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");

        SqlSessionFactory sqlSessionFactoryBean = cpx.getBean( SqlSessionFactory.class);

        SqlSession sqlSession = sqlSessionFactoryBean.openSession();

        TbBrandMapper mapper = sqlSession.getMapper(TbBrandMapper.class);

        System.out.println(mapper.findBrandById(1));

        TbBrand brand = new TbBrand();

        brand.setId(23);
        brand.setName("PXX");
        brand.setFirstChar("P");

        mapper.updateBrand(brand);
    }


    @Test
    public void test(){
        ClassPathXmlApplicationContext cpx  = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");

        SqlSessionFactory sqlSessionFactoryBean = cpx.getBean( SqlSessionFactory.class);

        SqlSession sqlSession = sqlSessionFactoryBean.openSession();

        TbBrandMapper mapper = sqlSession.getMapper(TbBrandMapper.class);

        TbBrand brand = new TbBrand();

        brand.setName(null);
        brand.setFirstChar("M");

        System.out.println(mapper.searchBrand(brand));
    }
}
