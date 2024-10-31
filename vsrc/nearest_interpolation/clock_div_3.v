module clock_div_3(
    input wire clk,
    input wire rst_n, 
    output wire clk_div3
);

reg divclk_1;
reg divclk_2;
reg divclk_3;
reg [1:0] cnt;

always @(posedge clk or negedge rst_n) begin
    if(~rst_n) begin
        cnt <= 2'd0;
    end else begin
        if(cnt == 2'd2) begin
            cnt <= 2'd0;
        end else begin
            cnt <= cnt + 1'b1;
        end
    end
end

always @(posedge clk or negedge rst_n) begin
    if(~rst_n) begin
        divclk_1 <= 1'b0;
    end else begin
        if(cnt == 0) begin
            divclk_1 <= ~divclk_1;
        end else begin
            divclk_1 <= divclk_1;
        end
    end
end

always @(posedge clk or negedge rst_n) begin
    if(~rst_n) begin
        divclk_2 <= 1'b0;
    end else begin
        if(cnt == 1) begin
            divclk_2 <= ~divclk_2;
        end else begin
            divclk_2 <= divclk_2;
        end
    end
end

always @(posedge clk or negedge rst_n) begin
    if(~rst_n) begin
        divclk_3 <= 1'b0;
    end else begin
        if(cnt == 2) begin
            divclk_3 <= ~divclk_3;
        end else begin
            divclk_3 <= divclk_3;
        end
    end
end

assign clk_div3 = divclk_1 ^ divclk_2;

endmodule