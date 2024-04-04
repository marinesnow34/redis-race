package test.redis.race;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {
	// 주문아이디 (UUID)
	@Id
	@Column(name = "order_id")
	private String orderId;

	// 주문번호(시퀀스)
	@Column(name = "order_number")
	private Long orderNumber;

	public Order(String orderId,Long orderNumber) {
		this.orderId = orderId;
		this.orderNumber = orderNumber;
	}
	public Order(Long orderNumber) {
		this.orderId = UUID.randomUUID().toString();
		this.orderNumber = orderNumber;
	}
}
